package de.starwit.persistence.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "detection")
public class DetectionEntity extends AbstractEntity<Long> {

    @Column(name = "stream_id", nullable = false)
    private String streamId;

    @Column(name = "object_id", nullable = false)
    private String objectId;

    @Column(name = "class_id", nullable = false)
    private int classId;

    @Column(name = "detection_timestamp", nullable = false)
    private ZonedDateTime detectionTimestamp;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "x")
    private Double x;

    @Column(name = "y")
    private Double y;

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public ZonedDateTime getDetectionTimestamp() {
        return detectionTimestamp;
    }

    public void setDetectionTimestamp(ZonedDateTime receiveTimestamp) {
        this.detectionTimestamp = receiveTimestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
