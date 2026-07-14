package de.starwit.service.impl;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.DetectionEntity;
import de.starwit.persistence.repository.BucketCountProjection;
import de.starwit.persistence.repository.DetectionRepository;
import de.starwit.persistence.repository.TimestampBounds;
import de.starwit.service.dto.ObjectCountBucketDto;
import de.starwit.service.dto.ObjectCountHistogramDto;

@Service
public class DetectionService implements ServiceInterface<DetectionEntity, DetectionRepository> {

    @Value("${detection.histogram.max-buckets:200}")
    private int maxBuckets;

    @Autowired
    private DetectionRepository detectionRepository;

    @Override
    public DetectionRepository getRepository() {
        return detectionRepository;
    }

    public List<DetectionEntity> findDetectionsBetween(OffsetDateTime start, OffsetDateTime end, String streamId) {
        return detectionRepository.findByStreamIdAndDetectionTimestampBetween(
                streamId, start.toZonedDateTime(), end.toZonedDateTime());
    }

    public ObjectCountHistogramDto getObjectCountHistogram(String streamId, int buckets) {
        TimestampBounds bounds = detectionRepository.findTimestampBoundsByStreamId(streamId);
        ZonedDateTime min = bounds != null ? bounds.min() : null;
        ZonedDateTime max = bounds != null ? bounds.max() : null;

        if (min == null || max == null) {
            return new ObjectCountHistogramDto(streamId, null, null, 0, new ArrayList<>());
        }

        int n = Math.max(1, Math.min(buckets, maxBuckets));
        long durationSeconds = Math.max(1, java.time.Duration.between(min, max).getSeconds());
        long bucketSeconds = Math.max(1, (long) Math.ceil((double) durationSeconds / n));

        Map<Long, Long> countsByBucket = new HashMap<>();
        for (BucketCountProjection row : detectionRepository.findObjectCountBuckets(streamId, min, max, bucketSeconds)) {
            countsByBucket.put(row.bucketIndex(), row.objectCount());
        }

        List<ObjectCountBucketDto> series = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ZonedDateTime bucketStart = min.plusSeconds((long) i * bucketSeconds);
            long count = countsByBucket.getOrDefault((long) i, 0L);
            series.add(new ObjectCountBucketDto(bucketStart.toOffsetDateTime(), count));
        }

        return new ObjectCountHistogramDto(streamId, min.toOffsetDateTime(), max.toOffsetDateTime(), bucketSeconds, series);
    }
}
