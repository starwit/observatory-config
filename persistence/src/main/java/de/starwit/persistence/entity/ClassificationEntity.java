package de.starwit.persistence.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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

    @NotBlank
    @Column(name = "tool_type")
    private String toolType;

    @NotBlank
    @Column(name = "color", nullable = false)
    private String color;

    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "classification", orphanRemoval = true, cascade = CascadeType.ALL)
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

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    

}
