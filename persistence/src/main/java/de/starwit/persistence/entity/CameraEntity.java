package de.starwit.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Camera Entity class
 */
@Entity
@Table(name = "camera")
public class CameraEntity extends AbstractEntity<Long> {

    // entity fields
    @NotBlank
    @Column(name = "saeid", nullable = false)
    private String saeId;

    // entity relations
    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "observationarea_id")
    private ObservationAreaEntity observationArea;

    public CameraEntity() {
    }
    
    public CameraEntity(String saeId, ObservationAreaEntity observationArea) {
        this.saeId = saeId;
        this.observationArea = observationArea;
    }

    // entity fields getters and setters
    public String getSaeId() {
        return saeId;
    }

    public void setSaeId(String saeId) {
        this.saeId = saeId;
    }

    // entity relations getters and setters
    public ObservationAreaEntity getObservationArea() {
        return observationArea;
    }

    public void setObservationArea(ObservationAreaEntity observationArea) {
        this.observationArea = observationArea;
    }
}
