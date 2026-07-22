package de.starwit.service.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class ObjectCountHistogramDto {

    private String streamId;
    private OffsetDateTime intervalStart;
    private OffsetDateTime intervalEnd;
    private long bucketWidthSeconds;
    private List<ObjectCountBucketDto> buckets;

    public ObjectCountHistogramDto(String streamId, OffsetDateTime intervalStart, OffsetDateTime intervalEnd,
            long bucketWidthSeconds, List<ObjectCountBucketDto> buckets) {
        this.streamId = streamId;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.bucketWidthSeconds = bucketWidthSeconds;
        this.buckets = buckets;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public OffsetDateTime getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(OffsetDateTime intervalStart) {
        this.intervalStart = intervalStart;
    }

    public OffsetDateTime getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(OffsetDateTime intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    public long getBucketWidthSeconds() {
        return bucketWidthSeconds;
    }

    public void setBucketWidthSeconds(long bucketWidthSeconds) {
        this.bucketWidthSeconds = bucketWidthSeconds;
    }

    public List<ObjectCountBucketDto> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<ObjectCountBucketDto> buckets) {
        this.buckets = buckets;
    }
}
