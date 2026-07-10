package de.starwit.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.persistence.repository.ImageRepository;
import de.starwit.service.dto.FileDto;
import de.starwit.visionapi.Sae.SaeMessage;

@Service
public class ImageService implements ServiceInterface<ImageEntity, ImageRepository> {

    static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ImageRepository imageRepository;

    // The Redis/Valkey template bean only exists when spring.data.redis.active=true;
    // inject it optionally so image upload/metadata handling keeps working without a broker.
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.frame-stream-prefix:saeapi-frame}")
    private String frameStreamPrefix;

    @Override
    public ImageRepository getRepository() {
        return imageRepository;
    }

    public ImageEntity saveAndFlush(ImageEntity entity) {
        return imageRepository.saveAndFlush(entity);
    }

    public List<ImageEntity> findByObservationAreaId(Long id) {
        return imageRepository.findByObservationAreaId(id);
    }

    public ImageEntity uploadImage(MultipartFile imageFile, ObservationAreaEntity observationAreaEntity)
            throws IOException {

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(observationAreaEntity.getName());
        imageEntity.setType(imageFile.getContentType());
        imageEntity.setData(imageFile.getBytes());

        ByteArrayInputStream bis = new ByteArrayInputStream(imageFile.getBytes());
        BufferedImage bImage = ImageIO.read(bis);
        observationAreaEntity.setImageHeight(bImage.getHeight());
        observationAreaEntity.setImageWidth(bImage.getWidth());
        imageEntity.setObservationArea(observationAreaEntity);
        observationAreaEntity.setImage(imageEntity);

        return imageRepository.save(imageEntity);
    }

    /**
     * Renews the observation area's image by fetching the most recent still frame from the
     * SAE frame stream in Valkey/Redis, decoding the JPEG and storing it as a fresh
     * {@link ImageEntity} (mirroring {@link #uploadImage}). The frame stream key is derived
     * from the first camera's SAE stream key: {@code <frameStreamPrefix>:<suffix>}, where the
     * suffix is the part of the SAE stream key after the first ':'.
     */
    public ImageEntity renewImage(ObservationAreaEntity observationAreaEntity) throws IOException, NotificationException {
        if (redisTemplate == null) {
            throw new NotificationException("error.image.renew.noredis",
                    "Cannot renew image because no Valkey/Redis connection is configured.");
        }

        String frameStreamKey = toFrameStreamKey(firstSaeStreamKey(observationAreaEntity));

        byte[] jpegBytes = fetchLatestFrame(frameStreamKey);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(observationAreaEntity.getName());
        imageEntity.setType("image/jpeg");
        imageEntity.setData(jpegBytes);

        BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(jpegBytes));
        if (bImage == null) {
            throw new NotificationException("error.image.renew.invalidframe",
                    "The latest frame could not be decoded as an image.");
        }
        observationAreaEntity.setImageHeight(bImage.getHeight());
        observationAreaEntity.setImageWidth(bImage.getWidth());
        imageEntity.setObservationArea(observationAreaEntity);
        observationAreaEntity.setImage(imageEntity);

        return imageRepository.save(imageEntity);
    }

    /**
     * Fetches the latest still frame from the SAE frame stream for the given raw SAE stream key
     * and returns the JPEG bytes, without touching any observation area.
     */
    public byte[] fetchFrameJpegByStreamKey(String saeStreamKey) throws NotificationException {
        if (redisTemplate == null) {
            throw new NotificationException("error.image.renew.noredis",
                    "Cannot fetch image because no Valkey/Redis connection is configured.");
        }
        if (saeStreamKey == null || saeStreamKey.isBlank()) {
            throw new NotificationException("error.image.renew.nostreamkey",
                    "No SAE stream key was provided.");
        }
        return fetchLatestFrame(toFrameStreamKey(saeStreamKey));
    }

    /**
     * Derives the Valkey/Redis frame-stream key from a SAE stream key:
     * {@code <frameStreamPrefix>:<suffix>}
     */
    private String toFrameStreamKey(String saeStreamKey) {
        String suffix = saeStreamKey.substring(saeStreamKey.indexOf(':') + 1);
        return frameStreamPrefix + ":" + suffix;
    }

    private String firstSaeStreamKey(ObservationAreaEntity observationAreaEntity) throws NotificationException {
        List<CameraEntity> cameras = observationAreaEntity.getCamera();
        if (cameras == null || cameras.isEmpty() || cameras.get(0).getSaeStreamKey() == null
                || cameras.get(0).getSaeStreamKey().isBlank()) {
            throw new NotificationException("error.image.renew.nostreamkey",
                    "The observation area has no camera with a SAE stream key.");
        }
        return cameras.get(0).getSaeStreamKey();
    }

    private byte[] fetchLatestFrame(String frameStreamKey) throws NotificationException {
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .reverseRange(frameStreamKey, Range.unbounded(), Limit.limit().count(1));

        if (records == null || records.isEmpty()) {
            throw new NotificationException("error.image.renew.noframe",
                    "No frame is currently available on stream " + frameStreamKey + ".");
        }

        Object protoData = records.get(0).getValue().get("proto_data_b64");
        if (protoData == null) {
            throw new NotificationException("error.image.renew.noframe",
                    "The latest stream entry does not contain frame data.");
        }

        try {
            SaeMessage saeMessage = SaeMessage.parseFrom(Base64.getDecoder().decode(protoData.toString()));
            byte[] jpegBytes = saeMessage.getFrame().getFrameDataJpeg().toByteArray();
            if (jpegBytes.length == 0) {
                throw new NotificationException("error.image.renew.noframe",
                        "The latest frame does not contain any JPEG image data.");
            }
            return jpegBytes;
        } catch (InvalidProtocolBufferException e) {
            LOG.error("Error decoding proto from frame stream {}", frameStreamKey, e);
            throw new NotificationException("error.image.renew.invalidframe",
                    "The latest frame could not be decoded.");
        }
    }

    public ImageEntity saveMetadata(ImageEntity entity) {
        if (entity != null) {
            if (entity.getId() != null) {
                ImageEntity newEntity = imageRepository.getReferenceById(entity.getId());
                newEntity.setName(entity.getName());
                newEntity.setData(entity.getData());
                newEntity.setType(entity.getType());
                return imageRepository.saveAndFlush(newEntity);
            }
            return imageRepository.saveAndFlush(entity);
        }
        return null;
    }

    public FileDto getImageAsFile(Long id) {
        ImageEntity image = imageRepository.getReferenceById(id);
        FileDto fileDto = new FileDto();
        fileDto.setByteArrayResource(new ByteArrayResource(image.getData()));
        fileDto.setFileSize(Long.valueOf(image.getData().length));
        fileDto.setName(image.getName());
        return fileDto;
    }

}
