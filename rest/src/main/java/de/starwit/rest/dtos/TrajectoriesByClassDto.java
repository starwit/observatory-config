package de.starwit.rest.dtos;

import java.util.List;

public class TrajectoriesByClassDto {
    private int classId;
    private List<TracedObjectDTO> tracedObjects;

    public TrajectoriesByClassDto(int classId, List<TracedObjectDTO> tracedObjects) {
        this.classId = classId;
        this.tracedObjects = tracedObjects;
    }

    public int getClassId() {
        return classId;
    }

    public List<TracedObjectDTO> getTracedObjects() {
        return tracedObjects;
    }
}
