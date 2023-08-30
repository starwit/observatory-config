package de.starwit.persistence.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Image Entity class
 */
@Entity
@Table(name = "image")
public class ImageEntity extends AbstractEntity<Long> {

    // entity fields
    @Column(name = "src")
    private String src;

    @Column(name = "name")
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
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
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
