package de.starwit.service.impl;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.DetectionEntity;
import de.starwit.persistence.repository.DetectionRepository;

@Service
public class DetectionService implements ServiceInterface<DetectionEntity, DetectionRepository> {

    @Autowired
    private DetectionRepository detectionRepository;

    @Override
    public DetectionRepository getRepository() {
        return detectionRepository;
    }

    public List<DetectionEntity> findDetectionsInWindow(OffsetDateTime timestamp, Duration window, String streamId) {
        ZonedDateTime end = timestamp.toZonedDateTime();
        ZonedDateTime start = end.minus(window);
        return detectionRepository.findByStreamIdAndDetectionTimestampBetween(streamId, start, end);
    }
}
