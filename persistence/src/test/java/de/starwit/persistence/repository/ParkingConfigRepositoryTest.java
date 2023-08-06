package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.starwit.persistence.entity.ParkingConfigEntity;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Tests for ParkingConfigRepository
 */
@DataJpaTest
public class ParkingConfigRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ParkingConfigRepository repository;

    @Test
    public void testFindAll() {
        List<ParkingConfigEntity> parkingconfigs = repository.findAll();
        assertTrue(parkingconfigs.isEmpty());
    }
}
