package de.starwit.persistence.repository;

import java.time.ZonedDateTime;

/**
 * Min/max {@code detectionTimestamp} bounds for a stream. Both are {@code null} when the stream has
 * no detections.
 */
public record TimestampBounds(ZonedDateTime min, ZonedDateTime max) {
}
