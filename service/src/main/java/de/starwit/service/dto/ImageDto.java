package de.starwit.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.starwit.persistence.entity.AbstractEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDto extends AbstractEntity<Long> {

    private String src;

    private String name;

    private List<RegionDto> regions;

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

}
