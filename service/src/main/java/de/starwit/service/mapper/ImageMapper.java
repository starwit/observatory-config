package de.starwit.service.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.service.dto.ImageDto;

@Component
public class ImageMapper implements CustomMapper<ImageEntity, ImageDto> {

    @Autowired
    PolygonMapper polygonMapper;

    @Override
    public ImageDto convertToDto(ImageEntity entity) {
        if (entity != null) {
            ImageDto dto = new ImageDto();
            dto.setId(entity.getId());
            dto.setData(entity.getData());
            dto.setName(entity.getName());
            dto.setType(entity.getType());
            if (entity.getObservationArea().getPolygon() != null) {
                dto.setRegions(polygonMapper.convertToDtoList(entity.getObservationArea().getPolygon()));
            }
            return dto;
        }
        return null;
    }

    @Override
    public ImageEntity convertToEntity(ImageDto dto) {
        if (dto != null) {
            ImageEntity entity = new ImageEntity();
            entity.setId(dto.getId());
            entity.setData(dto.getData());
            entity.setName(dto.getName());
            entity.setType(dto.getType());
            entity.getObservationArea().setPolygon(polygonMapper.convertToEntitySet(dto.getRegions()));
            return entity;
        }
        return null;
    }

    @Override
    public List<ImageDto> convertToDtoList(Collection<ImageEntity> entities) {
        List<ImageDto> dtos = new ArrayList<>();
        if (entities != null) {
            for (ImageEntity entity : entities) {
                dtos.add(convertToDto(entity));
            }
        }
        return dtos;
    }

}
