package de.starwit.service.impl;
import de.starwit.persistence.entity.ParkingAreaEntity;
import de.starwit.persistence.repository.ParkingAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import de.starwit.persistence.entity.ParkingConfigEntity;
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

    @Override
    public ParkingAreaRepository getRepository() {
        return parkingareaRepository;
    }


    @Override
    public ParkingAreaEntity saveOrUpdate(ParkingAreaEntity entity) {

        Set<ParkingConfigEntity> parkingConfigToSave = entity.getParkingConfig();

        if (entity.getId() != null) {
            ParkingAreaEntity entityPrev = this.findById(entity.getId());
            for (ParkingConfigEntity item : entityPrev.getParkingConfig()) {
                ParkingConfigEntity existingItem = parkingconfigRepository.getById(item.getId());
                existingItem.setParkingArea(null);
                this.parkingconfigRepository.save(existingItem);
            }
        }

        entity.setParkingConfig(null);
        entity = this.getRepository().save(entity);
        this.getRepository().flush();

        if (parkingConfigToSave != null && !parkingConfigToSave.isEmpty()) {
            for (ParkingConfigEntity item : parkingConfigToSave) {
                ParkingConfigEntity newItem = parkingconfigRepository.getById(item.getId());
                newItem.setParkingArea(entity);
                parkingconfigRepository.save(newItem);
            }
        }
        return this.getRepository().getById(entity.getId());
    }
}
