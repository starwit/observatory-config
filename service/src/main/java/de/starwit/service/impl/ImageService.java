package de.starwit.service.impl;

import java.util.List;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.PolygonRepository;

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
                newEntity.setSrc(entity.getSrc());
                return imageRepository.saveAndFlush(newEntity);
            }
            return imageRepository.saveAndFlush(entity);
        }
        return null;
    }
}
