package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.starwit.persistence.entity.ClassificationEntity;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Tests for ClassificationRepository
 */
@DataJpaTest
public class ClassificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClassificationRepository repository;

    @Test
    public void testFindAll() {
        List<ClassificationEntity> classifications = repository.findAll();
        assertTrue(classifications.isEmpty());
    }
}
