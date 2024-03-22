package de.starwit.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

/**
 * Polygon Entity class
 */
@Entity
@Table(name = "polygon", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "observationarea_id" })
})
public class PolygonEntity extends AbstractEntity<Long> {

    // entity fields
    @Column(name = "open")
    private Boolean open;

    // entity relations
    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "observationarea_id")
    private ObservationAreaEntity observationArea;

    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "classification_id")
    private ClassificationEntity classification;

    @JsonFilter("filterId")
    @OneToMany(mappedBy = "polygon", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PointEntity> point;

    @JsonFilter("filterId")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // entity fields getters and setters
    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    // entity relations getters and setters
    public ObservationAreaEntity getObservationArea() {
        return observationArea;
    }

    public void setObservationArea(ObservationAreaEntity observationArea) {
        this.observationArea = observationArea;
    }

    public ClassificationEntity getClassification() {
        return classification;
    }

    public void setClassification(ClassificationEntity classification) {
        this.classification = classification;
    }

    public List<PointEntity> getPoint() {
        return point;
    }

    public void setPoint(List<PointEntity> point) {
        this.point = point;
    }

    public void addToPoints(PointEntity child) {
        child.setPolygon(this);
        if (this.point == null) {
            this.point = new ArrayList<>();
        }
        this.point.add(child);
    }

}
