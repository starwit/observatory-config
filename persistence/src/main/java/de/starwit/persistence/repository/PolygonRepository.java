package de.starwit.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import de.starwit.persistence.entity.PolygonEntity;

/**
 * Polygon Repository class
 */
public interface PolygonRepository extends JpaRepository<PolygonEntity, Long> {

    @Query("SELECT e FROM PolygonEntity e WHERE NOT EXISTS (SELECT r FROM e.image r)")
    public List<PolygonEntity> findAllWithoutImage();

    @Query("SELECT e FROM PolygonEntity e WHERE NOT EXISTS (SELECT r FROM e.image r WHERE r.id <> ?1)")
    public List<PolygonEntity> findAllWithoutOtherImage(Long id);
}
