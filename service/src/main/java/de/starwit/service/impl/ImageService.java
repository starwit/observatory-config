package de.starwit.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
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

    @Override
    public ImageRepository getRepository() {
        return imageRepository;
    }

    public ImageEntity saveAndFlush(ImageEntity entity) {
        return imageRepository.saveAndFlush(entity);
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

    public ImageEntity uploadImage(MultipartFile imageFile, ParkingConfigEntity parkingConfigEntity)
            throws IOException {
        ImageEntity newEntity = new ImageEntity();
        newEntity.setName(imageFile.getOriginalFilename());
        newEntity.setType(imageFile.getContentType());
        newEntity.setData(imageFile.getBytes());
        newEntity.setParkingConfig(parkingConfigEntity);

        return imageRepository.save(newEntity);
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
}
