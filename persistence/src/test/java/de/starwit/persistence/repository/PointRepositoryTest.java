package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.starwit.persistence.entity.PointEntity;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Tests for PointRepository
 */
@DataJpaTest
public class PointRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PointRepository repository;

    @Test
    public void testFindAll() {
        List<PointEntity> points = repository.findAll();
        assertTrue(points.isEmpty());
    }
}
