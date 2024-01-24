package de.starwit.service.dto;

import java.util.List;

public class DatabackendDto {
    private String name;
    private Long parkingAreaId;
    private String type;
    private String cameraId;
    private Integer detectionClassId;
    private Boolean enabled;
    private List<GeometryPointsDto> geometryPoints;
    private String cls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParkingAreaId() {
        return parkingAreaId;
    }

    public void setParkingAreaId(Long parkingAreaId) {
        this.parkingAreaId = parkingAreaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public Integer getDetectionClassId() {
        return detectionClassId;
    }

    public void setDetectionClassId(Integer detectionClassId) {
        this.detectionClassId = detectionClassId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<GeometryPointsDto> getGeometryPoints() {
        return geometryPoints;
    }

    public void setGeometryPoints(List<GeometryPointsDto> geometryPoints) {
        this.geometryPoints = geometryPoints;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }
}