package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.entity.ParkingConfigEntity;

/**
 * Tests for ParkingConfigRepository
 */
@DataJpaTest
public class ParkingConfigRepositoryTest {

    @Autowired
    private ParkingConfigRepository repository;

    @Test
    public void testFindAll() {
        List<ParkingConfigEntity> parkingconfigs = repository.findAll();
        assertTrue(parkingconfigs.isEmpty());
    }
}
