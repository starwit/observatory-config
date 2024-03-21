package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.entity.ObservationAreaEntity;

/**
 * Tests for ObservationAreaRepository
 */
@DataJpaTest
public class ObservationAreaRepositoryTest {

    @Autowired
    private ObservationAreaRepository repository;

    @Test
    public void testFindAll() {
        List<ObservationAreaEntity> observationareas = repository.findAll();
        assertTrue(observationareas.isEmpty());
    }
}
