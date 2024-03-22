package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;

/**
 * Camera Repository class
 */
@Repository
public interface CameraRepository extends CustomRepository<CameraEntity, Long> {

    @Query("SELECT e FROM CameraEntity e WHERE NOT EXISTS (SELECT r FROM e.observationArea r)")
    public List<CameraEntity> findAllWithoutObservationArea();

    @Query("SELECT e FROM CameraEntity e WHERE NOT EXISTS (SELECT r FROM e.observationArea r WHERE r.id <> ?1)")
    public List<CameraEntity> findAllWithoutOtherObservationArea(Long id);

    @Query("SELECT e.id FROM CameraEntity e WHERE e.observationArea is null")
    public List<Long> findAllWithEmptyObservationArea();

    public List<CameraEntity> findBySaeIdAndObservationArea(String saeId, ObservationAreaEntity observationArea);

    public List<CameraEntity> findByObservationArea(ObservationAreaEntity observationArea);
}
