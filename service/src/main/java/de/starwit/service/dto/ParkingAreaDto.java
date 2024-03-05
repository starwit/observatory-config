package de.starwit.service.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ParkingAreaDto {

    private Long id;

    private String name;

    private Long selectedProdConfigId;

    List<String> saeIds;

    @Min(value = -90)
    @Max(value = 90)
    @Column(name = "top_left_latitude")
    private BigDecimal topleftlatitude;

    @Min(value = -180)
    @Max(value = 180)
    @Column(name = "top_left_longitude")
    private BigDecimal topleftlongitude;

    @Column(name = "degree_per_pixel_x")
    private BigDecimal degreeperpixelx;

    @Column(name = "degree_per_pixel_y")
    private BigDecimal degreeperpixely;

    @Column(name = "georeferenced")
    private Boolean geoReferenced;
   
    @Min(value = -90)
    @Max(value = 90)
    @Column(name = "center_latitude")
    private BigDecimal centerlatitude;

    @Min(value = -180)
    @Max(value = 180)
    @Column(name = "center_longitude")
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

    public Long getSelectedProdConfigId() {
        return selectedProdConfigId;
    }

    public void setSelectedProdConfigId(Long selectedProdConfigId) {
        this.selectedProdConfigId = selectedProdConfigId;
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
