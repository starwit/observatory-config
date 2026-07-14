package de.starwit.persistence.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.starwit.persistence.entity.DetectionEntity;

public interface DetectionRepository extends CustomRepository<DetectionEntity, Long> {

    List<DetectionEntity> findByStreamIdAndDetectionTimestampBetween(String streamId, ZonedDateTime start, ZonedDateTime end);

    @Query("SELECT new de.starwit.persistence.repository.TimestampBounds("
            + "MIN(d.detectionTimestamp), MAX(d.detectionTimestamp)) "
            + "FROM DetectionEntity d WHERE d.streamId = :streamId")
    TimestampBounds findTimestampBoundsByStreamId(@Param("streamId") String streamId);

    @Query(value = "SELECT cast(floor(extract(epoch from (detection_timestamp - :start)) / :bucketSeconds) as bigint) AS bucketIndex, "
            + "count(distinct object_id) AS objectCount "
            + "FROM detection "
            + "WHERE stream_id = :streamId AND detection_timestamp >= :start AND detection_timestamp <= :end "
            + "GROUP BY bucketIndex ORDER BY bucketIndex", nativeQuery = true)
    List<BucketCountProjection> findObjectCountBuckets(@Param("streamId") String streamId,
            @Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end,
            @Param("bucketSeconds") long bucketSeconds);
}
