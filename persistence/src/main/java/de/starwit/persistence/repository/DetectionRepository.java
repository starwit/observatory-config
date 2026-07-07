package de.starwit.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import de.starwit.persistence.entity.DetectionEntity;

public interface DetectionRepository extends CustomRepository<DetectionEntity, Long> {

    List<DetectionEntity> findByStreamIdAndDetectionTimestampBetween(String streamId, LocalDateTime start, LocalDateTime end);
}
