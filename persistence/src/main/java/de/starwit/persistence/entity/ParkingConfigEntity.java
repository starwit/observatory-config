package de.starwit.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

import java.time.ZonedDateTime;
import de.starwit.persistence.serializer.ZonedDateTimeSerializer;
import de.starwit.persistence.serializer.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.CascadeType;

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


    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version;


    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "parkingConfig")
    private Set<ImageEntity> image;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    // entity relations getters and setters
    public Set<ImageEntity> getImage() {
        return image;
    }

    public void setImage(Set<ImageEntity> image) {
        this.image = image;
    }

    public ParkingAreaEntity getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(ParkingAreaEntity parkingArea) {
        this.parkingArea = parkingArea;
    }

}
