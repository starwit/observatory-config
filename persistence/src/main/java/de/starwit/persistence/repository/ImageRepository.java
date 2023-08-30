package de.starwit.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import de.starwit.persistence.entity.ImageEntity;

/**
 * Image Repository class
 */
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    @Query("SELECT e FROM ImageEntity e WHERE NOT EXISTS (SELECT r FROM e.parkingConfig r)")
    public List<ImageEntity> findAllWithoutParkingConfig();

    @Query("SELECT e FROM ImageEntity e WHERE NOT EXISTS (SELECT r FROM e.parkingConfig r WHERE r.id <> ?1)")
    public List<ImageEntity> findAllWithoutOtherParkingConfig(Long id);
}
