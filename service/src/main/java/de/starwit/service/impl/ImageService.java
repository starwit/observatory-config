package de.starwit.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.persistence.repository.ImageRepository;

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
        ImageEntity imageEntity = new ImageEntity();
        List<ImageEntity> images = imageRepository.findByParkingConfigId(parkingConfigEntity.getId());
        if (images == null || images.isEmpty()) {
            imageEntity = new ImageEntity();
        } else {
            imageEntity = images.get(0);
        }
        imageEntity.setName(parkingConfigEntity.getName());
        imageEntity.setType(imageFile.getContentType());
        imageEntity.setData(imageFile.getBytes());
        imageEntity.setParkingConfig(parkingConfigEntity);
        return imageRepository.save(imageEntity);
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
