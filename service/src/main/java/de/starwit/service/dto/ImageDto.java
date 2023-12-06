package de.starwit.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.starwit.persistence.entity.AbstractEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDto extends AbstractEntity<Long> {

    private byte[] data;

    private String type;

    private String name;

    private List<RegionDto> regions;

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

}
