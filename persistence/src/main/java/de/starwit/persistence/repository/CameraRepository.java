package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.entity.CameraEntity;

/**
 * Camera Repository class
 */
@Repository
public interface CameraRepository extends CustomRepository<CameraEntity, Long> {

    @Query("SELECT e.id FROM CameraEntity e WHERE e.observationAreas is null OR e.observationAreas is empty")
    public List<Long> findAllWithEmptyObservationArea();

    public CameraEntity findBySaeStreamKey(String saeStreamKey);
}
