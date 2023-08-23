package de.starwit.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import de.starwit.persistence.entity.ParkingAreaEntity;

/**
 * ParkingArea Repository class
 */
@Repository
public interface ParkingAreaRepository extends JpaRepository<ParkingAreaEntity, Long> {

    @Query("SELECT e FROM ParkingAreaEntity e WHERE NOT EXISTS (SELECT r FROM e.selectedTestConfig r)")
    public List<ParkingAreaEntity> findAllWithoutSelectedTestConfig();

    @Query("SELECT e FROM ParkingAreaEntity e WHERE NOT EXISTS (SELECT r FROM e.selectedTestConfig r WHERE r.id <> ?1)")
    public List<ParkingAreaEntity> findAllWithoutOtherSelectedTestConfig(Long id);
    @Query("SELECT e FROM ParkingAreaEntity e WHERE NOT EXISTS (SELECT r FROM e.selectedProdConfig r)")
    public List<ParkingAreaEntity> findAllWithoutSelectedProdConfig();

    @Query("SELECT e FROM ParkingAreaEntity e WHERE NOT EXISTS (SELECT r FROM e.selectedProdConfig r WHERE r.id <> ?1)")
    public List<ParkingAreaEntity> findAllWithoutOtherSelectedProdConfig(Long id);
}
