package de.starwit.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import de.starwit.persistence.entity.PointEntity;

/**
 * Point Repository class
 */
@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> {

    @Query("SELECT e FROM PointEntity e WHERE NOT EXISTS (SELECT r FROM e.polygon r)")
    public List<PointEntity> findAllWithoutPolygon();

    @Query("SELECT e FROM PointEntity e WHERE NOT EXISTS (SELECT r FROM e.polygon r WHERE r.id <> ?1)")
    public List<PointEntity> findAllWithoutOtherPolygon(Long id);
}
