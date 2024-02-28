package de.starwit.service.dto;

import java.math.BigDecimal;

public class GeometryPointsDto {
    private Double x;
    private Double y;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer orderIdx;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    public Integer getOrderIdx() {
        return orderIdx;
    }
    
    public void setOrderIdx(Integer orderIdx) {
        this.orderIdx = orderIdx;
    }
}
