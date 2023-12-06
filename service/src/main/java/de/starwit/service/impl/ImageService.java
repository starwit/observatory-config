package de.starwit.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.repository.ImageRepository;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.PolygonRepository;
import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 
 * Image Service class
 *
 */
@Service
public class ImageService implements ServiceInterface<ImageEntity, ImageRepository> {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PolygonRepository polygonRepository;

    @Override
    public ImageRepository getRepository() {
        return imageRepository;
    }

    public List<ImageEntity> findAllWithoutParkingConfig() {
        return imageRepository.findAllWithoutParkingConfig();
    }

    public List<ImageEntity> findAllWithoutOtherParkingConfig(Long id) {
        return imageRepository.findAllWithoutOtherParkingConfig(id);
    }

    public List<ImageEntity> findByParkingConfigId(Long id) {
        return imageRepository.findByParkingConfigId(id);
    }

    public String uploadImage(MultipartFile imageFile) throws IOException {
        ImageEntity newEntity = new ImageEntity();
        newEntity.setName(imageFile.getOriginalFilename());
        newEntity.setType(imageFile.getContentType());
        newEntity.setData(compressImage(imageFile.getBytes()));

        imageRepository.save(newEntity);
        return "file uploaded successfully : " + imageFile.getOriginalFilename();
    }

    @Transactional
    public byte[] getImage(Long id) {
        Optional<ImageEntity> dbImage = imageRepository.findById(id);
        byte[] image = decompressImage(dbImage.get().getData());
        return image;
    }

    @Override
    public ImageEntity saveOrUpdate(ImageEntity entity) {

        Set<PolygonEntity> polygonToSave = entity.getPolygon();

        if (entity.getId() != null) {
            ImageEntity entityPrev = this.findById(entity.getId());
            if (entityPrev != null && entityPrev.getPolygon() != null) {
                for (PolygonEntity item : entityPrev.getPolygon()) {
                    PolygonEntity existingItem = polygonRepository.getReferenceById(item.getId());
                    existingItem.setImage(null);
                    this.polygonRepository.save(existingItem);
                }
            }
        }

        entity.setPolygon(null);
        entity = this.getRepository().save(entity);
        this.getRepository().flush();

        if (polygonToSave != null && !polygonToSave.isEmpty()) {
            for (PolygonEntity item : polygonToSave) {
                PolygonEntity newItem = polygonRepository.getReferenceById(item.getId());
                newItem.setImage(entity);
                polygonRepository.save(newItem);
            }
        }
        return this.getRepository().getReferenceById(entity.getId());
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

    public static byte[] compressImage(byte[] data) {

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception exception) {
        }
        return outputStream.toByteArray();
    }
}


