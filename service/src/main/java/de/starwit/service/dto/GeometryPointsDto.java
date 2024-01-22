package de.starwit.service.dto;

public class GeometryPointsDto {
    private Double x;
    private Double y;
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
    
    public Integer getOrderIdx() {
        return orderIdx;
    }
    
    public void setOrderIdx(Integer orderIdx) {
        this.orderIdx = orderIdx;
    }
}
