package de.starwit.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
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

    public List<ImageEntity> findByObservationAreaId(Long id) {
        return imageRepository.findByObservationAreaId(id);
    }

    public ImageEntity uploadImage(MultipartFile imageFile, ObservationAreaEntity observationAreaEntity)
            throws IOException {
        ImageEntity imageEntity = new ImageEntity();
        List<ImageEntity> images = imageRepository.findByObservationAreaId(observationAreaEntity.getId());
        if (images == null || images.isEmpty()) {
            imageEntity = new ImageEntity();
        } else {
            imageEntity = images.get(0);
        }
        imageEntity.setName(observationAreaEntity.getName());
        imageEntity.setType(imageFile.getContentType());
        imageEntity.setData(imageFile.getBytes());
        imageEntity.setObservationArea(observationAreaEntity);
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
