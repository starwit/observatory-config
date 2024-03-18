package de.starwit.service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.starwit.persistence.entity.AbstractEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDto extends AbstractEntity<Long> {

    private byte[] data;

    private String type;

    private String name;

    private List<RegionDto> regions;

    private int imageHeight;
    
    private int imageWidth;
    
    private BigDecimal degreeperpixelx;

    private BigDecimal degreeperpixely;

    private Boolean geoReferenced;

    private BigDecimal topleftlongitude;

    private BigDecimal topleftlatitude;

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

    public Boolean getGeoReferenced() {
        return geoReferenced;
    }

    public void setGeoReferenced(Boolean geoReferenced) {
        this.geoReferenced = geoReferenced;
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

    public List<RegionDto> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionDto> regions) {
        this.regions = regions;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    @JsonProperty("pixelSize")
    private void unpackImageDimensions(Map<String, Integer> image) {
        imageHeight = image.get("h");
        imageWidth = image.get("w");
    }

}
