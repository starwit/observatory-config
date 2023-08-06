package de.starwit.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

import java.time.ZonedDateTime;
import de.starwit.persistence.serializer.ZonedDateTimeSerializer;
import de.starwit.persistence.serializer.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.CascadeType;

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


    @Column(name = "activeconfigversion")
    private Integer activeConfigVersion;


    @Column(name = "testconfigversion")
    private Integer testConfigVersion;


    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "parkingArea")
    private Set<ParkingConfigEntity> parkingConfig;

    // entity fields getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getActiveConfigVersion() {
        return activeConfigVersion;
    }

    public void setActiveConfigVersion(Integer activeConfigVersion) {
        this.activeConfigVersion = activeConfigVersion;
    }

    public Integer getTestConfigVersion() {
        return testConfigVersion;
    }

    public void setTestConfigVersion(Integer testConfigVersion) {
        this.testConfigVersion = testConfigVersion;
    }

    // entity relations getters and setters
    public Set<ParkingConfigEntity> getParkingConfig() {
        return parkingConfig;
    }

    public void setParkingConfig(Set<ParkingConfigEntity> parkingConfig) {
        this.parkingConfig = parkingConfig;
    }

}
