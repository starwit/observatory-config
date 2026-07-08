package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import de.starwit.persistence.entity.ObservationAreaEntity;

/**
 * Tests for ObservationAreaRepository
 */
@DataJpaTestWithoutPartitions
public class ObservationAreaRepositoryTest {

    @Autowired
    private ObservationAreaRepository repository;

    @Test
    public void testFindAll() {
        List<ObservationAreaEntity> observationareas = repository.findAll();
        assertTrue(observationareas.isEmpty());
    }
}
