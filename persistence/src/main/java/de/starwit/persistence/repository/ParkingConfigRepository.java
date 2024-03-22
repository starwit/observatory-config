package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import de.starwit.persistence.entity.ParkingConfigEntity;

/**
 * ParkingConfig Repository class
 */
public interface ParkingConfigRepository extends CustomRepository<ParkingConfigEntity, Long> {

    @Query("SELECT e FROM ParkingConfigEntity e WHERE NOT EXISTS (SELECT r FROM e.observationArea r)")
    public List<ParkingConfigEntity> findAllWithoutObservationArea();

    @Query("SELECT e FROM ParkingConfigEntity e WHERE NOT EXISTS (SELECT r FROM e.observationArea r WHERE r.id <> ?1)")
    public List<ParkingConfigEntity> findAllWithoutOtherObservationArea(Long id);
}
