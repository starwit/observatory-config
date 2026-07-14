package de.starwit.service.impl;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.DetectionEntity;
import de.starwit.persistence.repository.BucketCountProjection;
import de.starwit.persistence.repository.DetectionRepository;
import de.starwit.service.dto.ObjectCountBucketDto;
import de.starwit.service.dto.ObjectCountHistogramDto;

@Service
public class DetectionService implements ServiceInterface<DetectionEntity, DetectionRepository> {

    // TODO This should be a property
    private static final int MAX_BUCKETS = 200;

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
        List<Object[]> boundsResult = detectionRepository.findTimestampBoundsByStreamId(streamId);
        Object[] bounds = boundsResult.isEmpty() ? null : boundsResult.get(0);
        ZonedDateTime min = bounds != null ? (ZonedDateTime) bounds[0] : null;
        ZonedDateTime max = bounds != null ? (ZonedDateTime) bounds[1] : null;

        if (min == null || max == null) {
            return new ObjectCountHistogramDto(streamId, null, null, 0, new ArrayList<>());
        }

        int n = Math.max(1, Math.min(buckets, MAX_BUCKETS));
        long durationSeconds = Math.max(1, java.time.Duration.between(min, max).getSeconds());
        long bucketSeconds = Math.max(1, (long) Math.ceil((double) durationSeconds / n));

        Map<Long, Long> countsByBucket = new HashMap<>();
        for (BucketCountProjection row : detectionRepository.findObjectCountBuckets(streamId, min, max, bucketSeconds)) {
            countsByBucket.put(row.getBucketIndex(), row.getObjectCount());
        }

        // TODO Why do we use n here? Are we absolutely sure that the query always returns n results?
        // TODO Also, can't we put this whole calculation into the sql query? S.t. it returns buckets already with timestamps to make this loop unnecessary?
        List<ObjectCountBucketDto> series = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ZonedDateTime bucketStart = min.plusSeconds((long) i * bucketSeconds);
            long count = countsByBucket.getOrDefault((long) i, 0L);
            series.add(new ObjectCountBucketDto(bucketStart.toOffsetDateTime(), count));
        }

        return new ObjectCountHistogramDto(streamId, min.toOffsetDateTime(), max.toOffsetDateTime(), bucketSeconds, series);
    }
}
