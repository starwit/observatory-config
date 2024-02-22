package de.starwit.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    @Column(name = "uuid", nullable = false)
    private String uuid;

    // entity relations
    @JsonFilter("filterId")
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "image_id", referencedColumnName = "id", unique = true)
    private ImageEntity image;

    // entity fields getters and setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    // entity relations getters and setters
    public ImageEntity getImage() {
        return image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

}
