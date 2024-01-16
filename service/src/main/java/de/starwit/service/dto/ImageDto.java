package de.starwit.service.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.starwit.persistence.entity.AbstractEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDto extends AbstractEntity<Long> {

    private String src;

    private String name;

    private List<RegionDto> regions;

    private int imageHeight;
    
    private int imageWidth;

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
