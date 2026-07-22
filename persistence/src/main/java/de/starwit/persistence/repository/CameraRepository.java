package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.entity.CameraEntity;

/**
 * Camera Repository class
 */
public interface CameraRepository extends CustomRepository<CameraEntity, Long> {

    @Query("SELECT e.id FROM CameraEntity e WHERE e.observationAreas is null OR e.observationAreas is empty")
    public List<Long> findAllWithEmptyObservationArea();

    public CameraEntity findBySaeStreamKey(String saeStreamKey);

    public List<CameraEntity> findByRecordingEnabledTrue();

    @Modifying
    @Transactional
    @Query("UPDATE CameraEntity c SET c.recordingEnabled = false WHERE c.recordingEnabled = true")
    int clearAllRecordingFlags();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM camera c WHERE NOT EXISTS (SELECT 1 FROM observationarea oa WHERE oa.camera_id = c.id)", nativeQuery = true)
    int deleteAllWithoutObservationAreas();
}
