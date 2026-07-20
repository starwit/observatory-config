package de.starwit.service.maintenance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.starwit.persistence.repository.CameraRepository;

@Service
public class CameraCleanupService {

    private static final Logger log = LoggerFactory.getLogger(CameraCleanupService.class);

    private final CameraRepository cameraRepository;

    public CameraCleanupService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    @Scheduled(cron = "${camera.cleanup.cron:0 0 3 * * *}")
    public void deleteOrphanCameras() {
        int deletedCount = cameraRepository.deleteAllWithoutObservationAreas();
        log.info("Deleted {} camera(s) without an observation area.", deletedCount);
    }
}
