package de.starwit.persistence.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "parkingArea")
    private Set<ParkingConfigEntity> parkingConfig;

    @JsonFilter("filterId")
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "testconfig_id", referencedColumnName = "id", unique = true)
    private ParkingConfigEntity selectedTestConfig;

    @JsonFilter("filterId")
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "prodconfig_id", referencedColumnName = "id", unique = true)
    private ParkingConfigEntity selectedProdConfig;

    // entity fields getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // entity relations getters and setters
    public Set<ParkingConfigEntity> getParkingConfig() {
        return parkingConfig;
    }

    public void setParkingConfig(Set<ParkingConfigEntity> parkingConfig) {
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

}
