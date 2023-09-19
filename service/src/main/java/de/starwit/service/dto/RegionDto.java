package de.starwit.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionDto {

    private String type = "polygon";

    private String id;

    private List<List<Double>> points;

    private Boolean open;

    private String color;

    private String cls;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<Double>> getPoints() {
        return points;
    }

    public void setPoints(List<List<Double>> points) {
        this.points = points;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
