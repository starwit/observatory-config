package de.starwit.service.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ObservationAreaDto {

    private Long id;

    private String name;

    List<String> saeIds;

    @Min(value = -90)
    @Max(value = 90)
    private BigDecimal topleftlatitude;

    @Min(value = -180)
    @Max(value = 180)
    private BigDecimal topleftlongitude;

    private BigDecimal degreeperpixelx;

    private BigDecimal degreeperpixely;

    private Boolean geoReferenced;
   
    @Min(value = -90)
    @Max(value = 90)
    private BigDecimal centerlatitude;

    @Min(value = -180)
    @Max(value = 180)
    private BigDecimal centerlongitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSaeIds() {
        return saeIds;
    }

    public void setSaeIds(List<String> saeIds) {
        this.saeIds = saeIds;
    }

    public BigDecimal getTopleftlatitude() {
        return topleftlatitude;
    }

    public void setTopleftlatitude(BigDecimal topleftlatitude) {
        this.topleftlatitude = topleftlatitude;
    }

    public BigDecimal getTopleftlongitude() {
        return topleftlongitude;
    }

    public void setTopleftlongitude(BigDecimal topleftlongitude) {
        this.topleftlongitude = topleftlongitude;
    }

    public BigDecimal getDegreeperpixelx() {
        return degreeperpixelx;
    }

    public void setDegreeperpixelx(BigDecimal degreeperpixelx) {
        this.degreeperpixelx = degreeperpixelx;
    }

    public BigDecimal getDegreeperpixely() {
        return degreeperpixely;
    }

    public void setDegreeperpixely(BigDecimal degreeperpixely) {
        this.degreeperpixely = degreeperpixely;
    }

    public Boolean getGeoReferenced() {
        return geoReferenced;
    }

    public void setGeoReferenced(Boolean geoReferenced) {
        this.geoReferenced = geoReferenced;
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
