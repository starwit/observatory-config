package de.starwit.persistence.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
/**
 * Polygon Entity class
 */
@Entity
@Table(name = "polygon")
public class PolygonEntity extends AbstractEntity<Long> {

    // entity fields
    @Column(name = "open")
    private Boolean open = false;

    // entity relations
    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageEntity image;

    @JsonFilter("filterId")
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
        name = "polygon_classification",
        joinColumns = @JoinColumn(name = "polygon_id"),
        inverseJoinColumns = @JoinColumn(name = "classification_id"))
    private Set<ClassificationEntity> classification = new HashSet<>();

    @JsonFilter("filterId")
    @OneToMany(mappedBy = "polygon")
    @OrderBy(value = "id ASC")
    private Set<PointEntity> point = new HashSet<>();

    // entity fields getters and setters
    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    // entity relations getters and setters
    public ImageEntity getImage() {
        return image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public Set<ClassificationEntity> getClassifications() {
        return classification;
    }

    public void setClassification(Set<ClassificationEntity> classification) {
        this.classification = classification;
    }

    public Set<PointEntity> getPoints() {
        return point;
    }

    public void setPoints(Set<PointEntity> point) {
        this.point = point;
    }

}
