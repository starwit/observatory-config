package de.starwit.persistence.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

/**
 * Point Entity class
 */
@Entity
@Table(name = "point")
public class PointEntity extends AbstractEntity<Long> {

    // entity fields
    @NotNull
    @Max(value = 1)
    @Column(name = "xvalue", nullable = false)
    private BigDecimal xvalue;

    @NotNull
    @Max(value = 1)
    @Column(name = "yvalue", nullable = false)
    private BigDecimal yvalue;

    // entity relations
    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "polygon_id")
    private PolygonEntity polygon;

    // entity fields getters and setters
    public BigDecimal getXvalue() {
        return xvalue;
    }

    public void setXvalue(BigDecimal xvalue) {
        this.xvalue = xvalue;
    }

    public BigDecimal getYvalue() {
        return yvalue;
    }

    public void setYvalue(BigDecimal yvalue) {
        this.yvalue = yvalue;
    }

    // entity relations getters and setters
    public PolygonEntity getPolygon() {
        return polygon;
    }

    public void setPolygon(PolygonEntity polygon) {
        this.polygon = polygon;
    }

}
