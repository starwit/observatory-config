package de.starwit.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;

import java.time.ZonedDateTime;
import de.starwit.persistence.serializer.ZonedDateTimeSerializer;
import de.starwit.persistence.serializer.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.CascadeType;

/**
 * Polygon Entity class
 */
@Entity
@Table(name = "polygon")
public class PolygonEntity extends AbstractEntity<Long> {

    // entity fields
    @Column(name = "open")
    private Boolean open;


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
    private Set<ClassificationEntity> classification;

    @JsonFilter("filterId")
    @OneToMany(mappedBy = "polygon")
    private Set<PointEntity> point;

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

    public Set<ClassificationEntity> getClassification() {
        return classification;
    }

    public void setClassification(Set<ClassificationEntity> classification) {
        this.classification = classification;
    }

    public Set<PointEntity> getPoint() {
        return point;
    }

    public void setPoint(Set<PointEntity> point) {
        this.point = point;
    }

}
