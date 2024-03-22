package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import de.starwit.persistence.entity.PolygonEntity;

/**
 * Polygon Repository class
 */
public interface PolygonRepository extends CustomRepository<PolygonEntity, Long> {

    @Query("SELECT e FROM PolygonEntity e WHERE NOT EXISTS (SELECT r FROM e.observationArea r)")
    public List<PolygonEntity> findAllWithoutObservationArea();

    @Query("SELECT e FROM PolygonEntity e WHERE NOT EXISTS (SELECT r FROM e.observationArea r WHERE r.id <> ?1)")
    public List<PolygonEntity> findAllWithoutOtherObservationArea(Long id);
}
