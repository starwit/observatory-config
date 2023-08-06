package de.starwit.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import de.starwit.persistence.entity.ParkingConfigEntity;

/**
 * ParkingConfig Repository class
 */
@Repository
public interface ParkingConfigRepository extends JpaRepository<ParkingConfigEntity, Long> {

    @Query("SELECT e FROM ParkingConfigEntity e WHERE NOT EXISTS (SELECT r FROM e.parkingArea r)")
    public List<ParkingConfigEntity> findAllWithoutParkingArea();

    @Query("SELECT e FROM ParkingConfigEntity e WHERE NOT EXISTS (SELECT r FROM e.parkingArea r WHERE r.id <> ?1)")
    public List<ParkingConfigEntity> findAllWithoutOtherParkingArea(Long id);
}
