package de.starwit.persistence.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * ParkingConfig Entity class
 */
@Entity
@Table(name = "parkingconfig")
public class ParkingConfigEntity extends AbstractEntity<Long> {

    // entity fields
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    // entity relations
    @JsonFilter("filterImage")
    @OneToMany(mappedBy = "parkingConfig", orphanRemoval = true)
    private List<ImageEntity> image;

    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "parkingarea_id")
    private ParkingAreaEntity parkingArea;

    // entity fields getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // entity relations getters and setters
    public List<ImageEntity> getImage() {
        return image;
    }

    public void setImage(List<ImageEntity> image) {
        this.image = image;
    }

    public ParkingAreaEntity getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(ParkingAreaEntity parkingArea) {
        this.parkingArea = parkingArea;
    }

}
