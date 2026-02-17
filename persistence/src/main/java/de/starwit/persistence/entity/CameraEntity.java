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
    @Column(name = "saestreamkey", nullable = false)
    private String saeStreamKey;

    // entity relations
    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "observationarea_id")
    private ObservationAreaEntity observationArea;

    public CameraEntity() {
    }
    
    public CameraEntity(String saeStreamKey, ObservationAreaEntity observationArea) {
        this.saeStreamKey = saeStreamKey;
        this.observationArea = observationArea;
    }

    // entity fields getters and setters
    public String getSaeStreamKey() {
        return saeStreamKey;
    }

    public void setSaeStreamKey(String saeStreamKey) {
        this.saeStreamKey = saeStreamKey;
    }

    // entity relations getters and setters
    public ObservationAreaEntity getObservationArea() {
        return observationArea;
    }

    public void setObservationArea(ObservationAreaEntity observationArea) {
        this.observationArea = observationArea;
    }
}
