package de.starwit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ParkingAreaEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.persistence.repository.ImageRepository;
import de.starwit.persistence.repository.ParkingAreaRepository;
import de.starwit.persistence.repository.ParkingConfigRepository;

/**
 * 
 * ParkingArea Service class
 *
 */
@Service
public class ParkingAreaService implements ServiceInterface<ParkingAreaEntity, ParkingAreaRepository> {

    @Autowired
    private ParkingAreaRepository parkingareaRepository;

    @Autowired
    private ParkingConfigRepository parkingconfigRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public ParkingAreaRepository getRepository() {
        return parkingareaRepository;
    }

    public List<ParkingAreaEntity> findAllWithoutSelectedTestConfig() {
        return parkingareaRepository.findAllWithoutSelectedTestConfig();
    }

    public List<ParkingAreaEntity> findAllWithoutOtherSelectedTestConfig(Long id) {
        return parkingareaRepository.findAllWithoutOtherSelectedTestConfig(id);
    }

    public List<ParkingAreaEntity> findAllWithoutSelectedProdConfig() {
        return parkingareaRepository.findAllWithoutSelectedProdConfig();
    }

    public List<ParkingAreaEntity> findAllWithoutOtherSelectedProdConfig(Long id) {
        return parkingareaRepository.findAllWithoutOtherSelectedProdConfig(id);
    }

    @Override
    public ParkingAreaEntity saveOrUpdate(ParkingAreaEntity entity) {

        if (entity.getId() != null) {
            ParkingAreaEntity entityPrev = this.findById(entity.getId());
            entityPrev.setName(entity.getName());
            entity = entityPrev;

        }

        entity = this.getRepository().saveAndFlush(entity);

        if (entity.getParkingConfig() == null || entity.getParkingConfig().isEmpty()) {
            ParkingConfigEntity p = new ParkingConfigEntity();
            p.setName(entity.getName() + "-config");
            p.setParkingArea(entity);
            entity.setSelectedProdConfig(p);
            p = parkingconfigRepository.saveAndFlush(p);
            ImageEntity image = new ImageEntity();
            image.setSrc("parking_south.jpg");
            image.setName(entity.getName());
            image.setParkingConfig(p);
            imageRepository.saveAndFlush(image);
        }
        return this.getRepository().getReferenceById(entity.getId());
    }
}
