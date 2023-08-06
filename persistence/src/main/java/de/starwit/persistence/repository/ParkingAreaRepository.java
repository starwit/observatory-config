package de.starwit.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.starwit.persistence.entity.ParkingAreaEntity;

/**
 * ParkingArea Repository class
 */
@Repository
public interface ParkingAreaRepository extends JpaRepository<ParkingAreaEntity, Long> {

}
