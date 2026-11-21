package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import de.starwit.persistence.TestcontainersConfiguration;
import de.starwit.persistence.entity.PolygonEntity;

/**
 * Tests for PolygonRepository
 */
@DataJpaTest
@Import(TestcontainersConfiguration.class)
public class PolygonRepositoryTest {

    @Autowired
    private PolygonRepository repository;

    @Test
    public void testFindAll() {
        List<PolygonEntity> polygons = repository.findAll();
        assertTrue(polygons.isEmpty());
    }
}
