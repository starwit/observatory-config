package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import de.starwit.persistence.entity.ImageEntity;

/**
 * Image Repository class
 */
public interface ImageRepository extends CustomRepository<ImageEntity, Long> {

    @Query("SELECT e FROM ImageEntity e WHERE e.observationArea.id = ?1")
    public List<ImageEntity> findByObservationAreaId(Long id);
}
