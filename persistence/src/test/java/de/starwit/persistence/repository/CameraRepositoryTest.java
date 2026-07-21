package de.starwit.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;

/**
 * Tests for CameraRepository
 */
@DataJpaTest
public class CameraRepositoryTest {

    @Autowired
    private CameraRepository cameraRepository;

    @Autowired
    private ObservationAreaRepository observationAreaRepository;

    @Test
    public void testDeleteAllWithoutObservationAreas() {
        CameraEntity linkedCamera = new CameraEntity();
        linkedCamera.setSaeStreamKey("linked-stream");
        cameraRepository.saveAndFlush(linkedCamera);

        ObservationAreaEntity observationArea = new ObservationAreaEntity();
        observationArea.setName("area-1");
        observationArea.setCamera(linkedCamera);
        observationAreaRepository.saveAndFlush(observationArea);

        CameraEntity orphanCamera = new CameraEntity();
        orphanCamera.setSaeStreamKey("orphan-stream");
        cameraRepository.saveAndFlush(orphanCamera);

        int deletedCount = cameraRepository.deleteAllWithoutObservationAreas();

        assertThat(deletedCount).isEqualTo(1);
        assertThat(cameraRepository.findAll())
                .extracting(camera -> camera.getSaeStreamKey())
                .containsExactly("linked-stream");
    }
}
