package de.starwit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.persistence.repository.ImageRepository;
import de.starwit.persistence.repository.ParkingConfigRepository;

/**
 * 
 * ParkingConfig Service class
 *
 */
@Service
public class ParkingConfigService implements ServiceInterface<ParkingConfigEntity, ParkingConfigRepository> {

    @Autowired
    private ParkingConfigRepository parkingconfigRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public ParkingConfigRepository getRepository() {
        return parkingconfigRepository;
    }

    public List<ParkingConfigEntity> findAllWithoutObservationArea() {
        return parkingconfigRepository.findAllWithoutObservationArea();
    }

    public List<ParkingConfigEntity> findAllWithoutOtherObservationArea(Long id) {
        return parkingconfigRepository.findAllWithoutOtherObservationArea(id);
    }

    @Override
    public ParkingConfigEntity saveOrUpdate(ParkingConfigEntity entity) {

        List<ImageEntity> imageToSave = entity.getImage();

        if (entity.getId() != null) {
            ParkingConfigEntity entityPrev = this.findById(entity.getId());
            for (ImageEntity item : entityPrev.getImage()) {
                ImageEntity existingItem = imageRepository.getReferenceById(item.getId());
                existingItem.setParkingConfig(null);
                this.imageRepository.save(existingItem);
            }
        }

        entity.setImage(null);
        entity = this.getRepository().save(entity);
        this.getRepository().flush();

        if (imageToSave != null && !imageToSave.isEmpty()) {
            for (ImageEntity item : imageToSave) {
                ImageEntity newItem = imageRepository.getReferenceById(item.getId());
                newItem.setParkingConfig(entity);
                imageRepository.save(newItem);
            }
        }
        return this.getRepository().getReferenceById(entity.getId());
    }
}
