package de.starwit.service.mapper;

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
            dto.setPoints(pointMapper.convertToDtoList(entity.getPoint()));
            dto.setOpen(entity.getOpen());
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
