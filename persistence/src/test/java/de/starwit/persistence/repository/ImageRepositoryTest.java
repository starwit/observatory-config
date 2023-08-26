package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import de.starwit.persistence.entity.ImageEntity;

/**
 * Tests for ImageRepository
 */
@DataJpaTest
public class ImageRepositoryTest {

    @Autowired
    private ImageRepository repository;

    @Test
    public void testFindAll() {
        List<ImageEntity> images = repository.findAll();
        assertTrue(images.isEmpty());
    }
}
