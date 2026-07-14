package de.starwit.persistence.repository;

/**
 * Projection for the bucketed unique-object-count aggregation query in {@link DetectionRepository}.
 */
public interface BucketCountProjection {
// TODO This should be a record
    Long getBucketIndex();

    Long getObjectCount();
}
