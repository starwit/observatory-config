package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.entity.PointEntity;

/**
 * Tests for PointRepository
 */
@DataJpaTest
public class PointRepositoryTest {

    @Autowired
    private PointRepository repository;

    @Test
    public void testFindAll() {
        List<PointEntity> points = repository.findAll();
        assertTrue(points.isEmpty());
    }
}
