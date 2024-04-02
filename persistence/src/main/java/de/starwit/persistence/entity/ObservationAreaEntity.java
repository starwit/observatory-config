package de.starwit.persistence.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * ObservationArea Entity class
 */
@Entity
@Table(name = "observationarea")
public class ObservationAreaEntity extends AbstractEntity<Long> {

    // entity fields
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "observationArea", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<PolygonEntity> polygon;

    // entity relations
    @JsonFilter("filterIdName")
    @OneToOne(mappedBy = "observationArea", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private ImageEntity image;

    @JsonFilter("filterCamera")
    @OneToMany(mappedBy = "observationArea", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<CameraEntity> camera;
  
    @Min(value = -90)
    @Max(value = 90)
    @Column(name = "center_latitude")
    private BigDecimal centerlatitude;

    @Min(value = -180)
    @Max(value = 180)
    @Column(name = "center_longitude")
    private BigDecimal centerlongitude;

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

    @Column(name = "processing_enabled")
    private Boolean processingEnabled;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "image_width")
    private Integer imageWidth;

    // entity fields getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageEntity getImage() {
        return image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public List<CameraEntity> getCamera() {
        return camera;
    }

    public void setCamera(List<CameraEntity> camera) {
        this.camera = camera;
    }

    // entity relations getters and setters
    public Set<PolygonEntity> getPolygon() {
        return polygon;
    }

    public void setPolygon(Set<PolygonEntity> polygon) {
        this.polygon = polygon;
    }

    public void addToPolygons(PolygonEntity child) {
        child.setObservationArea(this);
        if (this.polygon == null) {
            this.polygon = new HashSet<>();
        }
        this.polygon.add(child);
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

    public Boolean getProcessingEnabled() {
        return processingEnabled;
    }

    public void setProcessingEnabled(Boolean processingEnabled) {
        this.processingEnabled = processingEnabled;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

}
