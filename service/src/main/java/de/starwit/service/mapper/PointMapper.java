package de.starwit.service.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import de.starwit.persistence.entity.PointEntity;

@Component
public class PointMapper implements CustomMapper<PointEntity, List<Double>> {

    @Override
    public List<Double> convertToDto(PointEntity entity) {
        List<Double> dto = new ArrayList<>();
        if (entity != null && entity.getXvalue() != null && entity.getYvalue() != null) {
            dto.add(entity.getXvalue().doubleValue());
            dto.add(entity.getYvalue().doubleValue());
        }
        return dto;
    }

    @Override
    public PointEntity convertToEntity(List<Double> dto) {
        PointEntity entity = new PointEntity();
        if (dto != null && dto.size() > 1) {
            entity.setXvalue(BigDecimal.valueOf(dto.get(0)));
            entity.setYvalue(BigDecimal.valueOf(dto.get(1)));
        }
        return entity;
    }

}
