package de.starwit.persistence.repository;

/**
 * Projection for the bucketed unique-object-count aggregation query in {@link DetectionRepository}:
 * the bucket index and the number of distinct objects seen within it. Component names must match the
 * query's column aliases ({@code bucketIndex}, {@code objectCount}).
 */
public record BucketCountProjection(Long bucketIndex, Long objectCount) {
}
