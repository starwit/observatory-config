package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.starwit.persistence.entity.ParkingAreaEntity;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Tests for ParkingAreaRepository
 */
@DataJpaTest
public class ParkingAreaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ParkingAreaRepository repository;

    @Test
    public void testFindAll() {
        List<ParkingAreaEntity> parkingareas = repository.findAll();
        assertTrue(parkingareas.isEmpty());
    }
}
