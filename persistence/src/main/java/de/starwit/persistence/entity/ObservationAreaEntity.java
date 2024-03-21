package de.starwit.persistence.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * ObservationArea Entity class
 */
@Entity
@Table(name = "observationarea")
public class ObservationAreaEntity extends AbstractEntity<Long> {

    // entity fields
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    // entity relations
    @JsonFilter("filterImage")
    @OneToOne(mappedBy = "observationArea", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private ImageEntity image;
  
    @Min(value = -90)
    @Max(value = 90)
    @Column(name = "center_latitude")
    private BigDecimal centerlatitude;

    @Min(value = -180)
    @Max(value = 180)
    @Column(name = "center_longitude")
    private BigDecimal centerlongitude;

    // entity fields getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageEntity getImage() {
        return image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public BigDecimal getCenterlatitude() {
        return centerlatitude;
    }

    public void setCenterlatitude(BigDecimal centerlatitude) {
        this.centerlatitude = centerlatitude;
    }

    public BigDecimal getCenterlongitude() {
        return centerlongitude;
    }

    public void setCenterlongitude(BigDecimal centerlongitude) {
        this.centerlongitude = centerlongitude;
    }

}
