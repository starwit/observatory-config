package de.starwit.service.dto;

import java.time.OffsetDateTime;

public class ObjectCountBucketDto {

    private OffsetDateTime timestamp;
    private long objectCount;

    public ObjectCountBucketDto(OffsetDateTime timestamp, long objectCount) {
        this.timestamp = timestamp;
        this.objectCount = objectCount;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public long getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(long objectCount) {
        this.objectCount = objectCount;
    }
}
