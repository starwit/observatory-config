package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.starwit.persistence.entity.PolygonEntity;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Tests for PolygonRepository
 */
@DataJpaTest
public class PolygonRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PolygonRepository repository;

    @Test
    public void testFindAll() {
        List<PolygonEntity> polygons = repository.findAll();
        assertTrue(polygons.isEmpty());
    }
}
