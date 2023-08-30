package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.entity.PolygonEntity;

/**
 * Tests for PolygonRepository
 */
@DataJpaTest
public class PolygonRepositoryTest {

    @Autowired
    private PolygonRepository repository;

    @Test
    public void testFindAll() {
        List<PolygonEntity> polygons = repository.findAll();
        assertTrue(polygons.isEmpty());
    }
}
