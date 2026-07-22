package de.starwit.persistence.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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

    @Column(name = "recording_enabled", nullable = false)
    private boolean recordingEnabled = false;

    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "camera")
    private Set<ObservationAreaEntity> observationAreas;

    public CameraEntity() {
    }

    public CameraEntity(String saeStreamKey) {
        this.saeStreamKey = saeStreamKey;
    }

    // entity fields getters and setters
    public String getSaeStreamKey() {
        return saeStreamKey;
    }

    public void setSaeStreamKey(String saeStreamKey) {
        this.saeStreamKey = saeStreamKey;
    }

    public boolean isRecordingEnabled() {
        return recordingEnabled;
    }

    public void setRecordingEnabled(boolean recordingEnabled) {
        this.recordingEnabled = recordingEnabled;
    }

    // entity relations getters and setters
    public Set<ObservationAreaEntity> getObservationAreas() {
        return observationAreas;
    }

    public void setObservationAreas(Set<ObservationAreaEntity> observationAreas) {
        this.observationAreas = observationAreas;
    }
}
