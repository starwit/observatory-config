package de.starwit.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Image Entity class
 */
@Entity
@Table(name = "image")
public class ImageEntity extends AbstractEntity<Long> {

    // entity fields
    @Column(name = "data", nullable = false)
    private byte[] data;

    private String type;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "image", orphanRemoval = true)
    private Set<PolygonEntity> polygon;

    @JsonFilter("filterId")
    @OneToOne
    @JoinColumn(name = "observationarea_id")
    private ObservationAreaEntity observationArea;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "image_width")
    private Integer imageWidth;

    // entity fields getters and setters
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // entity relations getters and setters
    public Set<PolygonEntity> getPolygon() {
        return polygon;
    }

    public void setPolygon(Set<PolygonEntity> polygon) {
        this.polygon = polygon;
    }

    public ObservationAreaEntity getObservationArea() {
        return observationArea;
    }

    public void setObservationArea(ObservationAreaEntity observationArea) {
        this.observationArea = observationArea;
    }

    public void addToPolygons(PolygonEntity child) {
        child.setImage(this);
        if (this.polygon == null) {
            this.polygon = new HashSet<>();
        }
        this.polygon.add(child);
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

}
