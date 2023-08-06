package de.starwit.service.impl;
import java.util.List;
import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.persistence.repository.ParkingConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.repository.ImageRepository;

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

    public List<ParkingConfigEntity> findAllWithoutParkingArea() {
        return parkingconfigRepository.findAllWithoutParkingArea();
    }

    public List<ParkingConfigEntity> findAllWithoutOtherParkingArea(Long id) {
        return parkingconfigRepository.findAllWithoutOtherParkingArea(id);
    }

    @Override
    public ParkingConfigEntity saveOrUpdate(ParkingConfigEntity entity) {

        Set<ImageEntity> imageToSave = entity.getImage();

        if (entity.getId() != null) {
            ParkingConfigEntity entityPrev = this.findById(entity.getId());
            for (ImageEntity item : entityPrev.getImage()) {
                ImageEntity existingItem = imageRepository.getById(item.getId());
                existingItem.setParkingConfig(null);
                this.imageRepository.save(existingItem);
            }
        }

        entity.setImage(null);
        entity = this.getRepository().save(entity);
        this.getRepository().flush();

        if (imageToSave != null && !imageToSave.isEmpty()) {
            for (ImageEntity item : imageToSave) {
                ImageEntity newItem = imageRepository.getById(item.getId());
                newItem.setParkingConfig(entity);
                imageRepository.save(newItem);
            }
        }
        return this.getRepository().getById(entity.getId());
    }
}
