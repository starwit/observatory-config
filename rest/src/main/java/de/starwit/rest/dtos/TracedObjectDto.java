package de.starwit.rest.dtos;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.geo.Point;

public class TracedObjectDto {
    String objectId;
    OffsetDateTime start;
    OffsetDateTime end;
    List<Point> trajectory;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }

    public List<Point> getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(List<Point> trajectory) {
        this.trajectory = trajectory;
    }
}
