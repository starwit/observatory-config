package de.starwit.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import de.starwit.persistence.TestcontainersConfiguration;
import de.starwit.persistence.entity.ClassificationEntity;

/**
 * Tests for ClassificationRepository
 */
@DataJpaTest
@Import(TestcontainersConfiguration.class)
public class ClassificationRepositoryTest {

    @Autowired
    private ClassificationRepository repository;

    @Test
    public void testFindAllReturnsClassificationsSeededByFlyway() {
        List<ClassificationEntity> classifications = repository.findAll();
        assertThat(classifications)
                .extracting(ClassificationEntity::getName)
                .containsExactlyInAnyOrder("lightbarrier", "block", "observe", "flow");
    }
}
