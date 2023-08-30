package de.starwit.persistence.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Classification Entity class
 */
@Entity
@Table(name = "classification")
public class ClassificationEntity extends AbstractEntity<Long> {

    // entity fields
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    // entity relations
    @JsonFilter("filterId")
    @ManyToMany(mappedBy = "classification")
    private Set<PolygonEntity> polygon;

    // entity fields getters and setters
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

}
