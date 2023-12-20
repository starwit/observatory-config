package de.starwit.service.mapper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.service.dto.RegionDto;

@Component
public class PolygonMapper implements CustomMapper<PolygonEntity, RegionDto> {

    static Logger LOG = LoggerFactory.getLogger(PolygonMapper.class);

    @Autowired
    private PointMapper pointMapper;

    @Override
    public RegionDto convertToDto(PolygonEntity entity) {
        if (entity != null) {
            RegionDto dto = new RegionDto();
            dto.setId(entity.getId().toString());
            if (entity.getClassification() != null && !entity.getClassification().isEmpty()
                    && entity.getClassification().iterator().hasNext()) {
                ClassificationEntity cls = entity.getClassification().iterator().next();
                dto.setCls(cls.getName());
                dto.setColor(cls.getColor());
            }
            dto.setOpen(entity.getOpen());
            
            List<List<Double>> points = pointMapper.convertToDtoList(entity.getPoint());
            if (points.size() == 2) {
                dto.setType("line");
                dto.setX1(points.get(0).get(0));
                dto.setY1(points.get(0).get(1));
                dto.setX2(points.get(1).get(0));
                dto.setY2(points.get(1).get(1));
            } else {
                dto.setType("polygon");
                dto.setPoints(points);
            }

            return dto;
        }
        return null;
    }

    @Override
    public PolygonEntity convertToEntity(RegionDto dto) {
        if (dto != null) {
            PolygonEntity entity = new PolygonEntity();
            entity.setOpen(dto.getOpen());
            entity.setPoint(pointMapper.convertToEntityList(dto.getPoints()));
            try {
                entity.setId(Long.parseLong(dto.getId()));
            } catch (NumberFormatException e) {
                LOG.debug("id not valid for polygon: {}", dto.getId());
            }
            return entity;
        }
        return null;
    }

}
