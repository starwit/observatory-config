package de.starwit.rest.dtos;

import java.util.List;

public class TrajectoriesByClassDto {
    private int classId;
    private List<TracedObjectDto> tracedObjects;

    public TrajectoriesByClassDto(int classId, List<TracedObjectDto> tracedObjects) {
        this.classId = classId;
        this.tracedObjects = tracedObjects;
    }

    public int getClassId() {
        return classId;
    }

    public List<TracedObjectDto> getTracedObjects() {
        return tracedObjects;
    }
}
