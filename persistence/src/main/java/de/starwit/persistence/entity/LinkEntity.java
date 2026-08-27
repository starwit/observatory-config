package de.starwit.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Link Entity class for storing external links associated with ObservationArea
 */
@Entity
@Table(name = "link")
public class LinkEntity extends AbstractEntity<Long> {

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @ManyToOne
    @JoinColumn(name = "observation_area_id", nullable = false)
    private ObservationAreaEntity observationArea;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ObservationAreaEntity getObservationArea() {
        return observationArea;
    }

    public void setObservationArea(ObservationAreaEntity observationArea) {
        this.observationArea = observationArea;
    }
}
