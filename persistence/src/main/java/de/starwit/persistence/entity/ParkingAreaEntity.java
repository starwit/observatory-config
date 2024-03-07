package de.starwit.persistence.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * ParkingArea Entity class
 */
@Entity
@Table(name = "parkingarea")
public class ParkingAreaEntity extends AbstractEntity<Long> {

    // entity fields
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    // entity relations
    @JsonFilter("filterIdNameImageCamera")
    @OneToMany(mappedBy = "parkingArea", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ParkingConfigEntity> parkingConfig;

    @JsonFilter("filterIdNameImageCamera")
    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "testconfig_id", referencedColumnName = "id", unique = true)
    private ParkingConfigEntity selectedTestConfig;

    @JsonFilter("filterIdNameImageCamera")
    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "prodconfig_id", referencedColumnName = "id", unique = true)
    private ParkingConfigEntity selectedProdConfig;
  
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

    // entity relations getters and setters
    public List<ParkingConfigEntity> getParkingConfig() {
        return parkingConfig;
    }

    public void setParkingConfig(List<ParkingConfigEntity> parkingConfig) {
        this.parkingConfig = parkingConfig;
    }

    public ParkingConfigEntity getSelectedTestConfig() {
        return selectedTestConfig;
    }

    public void setSelectedTestConfig(ParkingConfigEntity selectedTestConfig) {
        this.selectedTestConfig = selectedTestConfig;
    }

    public ParkingConfigEntity getSelectedProdConfig() {
        return selectedProdConfig;
    }

    public void setSelectedProdConfig(ParkingConfigEntity selectedProdConfig) {
        this.selectedProdConfig = selectedProdConfig;
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
