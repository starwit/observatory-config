package de.starwit.persistence.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Image Entity class
 */
@Entity
@Table(name = "image")
public class ImageEntity extends AbstractEntity<Long> {

    // entity fields
    @Column(name = "data", nullable = false)
    private byte[] data;

    private String type;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "image")
    private Set<PolygonEntity> polygon;

    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "parkingconfig_id")
    private ParkingConfigEntity parkingConfig;

    // entity fields getters and setters
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public ParkingConfigEntity getParkingConfig() {
        return parkingConfig;
    }

    public void setParkingConfig(ParkingConfigEntity parkingConfig) {
        this.parkingConfig = parkingConfig;
    }

}
