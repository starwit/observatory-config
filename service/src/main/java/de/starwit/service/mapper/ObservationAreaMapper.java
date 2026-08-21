package de.starwit.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.LinkEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.service.dto.ObservationAreaDto;

public class ObservationAreaMapper implements CustomMapper<ObservationAreaEntity, ObservationAreaDto> {

    private LinkMapper linkMapper = new LinkMapper();

    @Override
    public ObservationAreaDto convertToDto(ObservationAreaEntity entity) {
        if (entity == null) {
            return null;
        }
        ObservationAreaDto dto = new ObservationAreaDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if (entity.getImage() == null || entity.getImage().getData() == null) {
            dto.setImage(null);
        } else {
            dto.setImage(entity.getImage());
        }
        dto.setCenterlatitude(entity.getCenterlatitude());
        dto.setCenterlongitude(entity.getCenterlongitude());
        if (entity.getCamera() != null) {
            dto.setSaeStreamKey(entity.getCamera().getSaeStreamKey());
        }
        dto.setDegreeperpixelx(entity.getDegreeperpixelx());
        dto.setDegreeperpixely(entity.getDegreeperpixely());
        dto.setGeoReferenced(entity.getGeoReferenced());
        dto.setTopleftlatitude(entity.getTopleftlatitude());
        dto.setTopleftlongitude(entity.getTopleftlongitude());
        dto.setProcessingEnabled(entity.getProcessingEnabled());
        if (entity.getLinks() != null) {
            dto.setLinks(entity.getLinks().stream()
                    .map(linkMapper::convertToDto)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    @Override
    public ObservationAreaEntity convertToEntity(ObservationAreaDto dto) {
        if (dto == null) {
            return null;
        }
        ObservationAreaEntity entity = new ObservationAreaEntity();
        return convertToEntity(dto, entity);
    }

    public ObservationAreaEntity convertToEntity(ObservationAreaDto dto, ObservationAreaEntity entity) {
        if (dto == null) {
            return null;
        }
        if (entity == null) {
            entity = new ObservationAreaEntity();
        }
        final ObservationAreaEntity finalEntity = entity;
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCenterlatitude(dto.getCenterlatitude());
        entity.setCenterlongitude(dto.getCenterlongitude());
        entity.setDegreeperpixelx(dto.getDegreeperpixelx());
        entity.setDegreeperpixely(dto.getDegreeperpixely());
        entity.setTopleftlatitude(dto.getTopleftlatitude());
        entity.setTopleftlongitude(dto.getTopleftlongitude());
        entity.setGeoReferenced(dto.getGeoReferenced());
        entity.setProcessingEnabled(dto.getProcessingEnabled());
        if (dto.getLinks() != null) {
            Set<LinkEntity> linkEntities = dto.getLinks().stream()
                    .map(linkMapper::convertToEntity)
                    .collect(Collectors.toSet());
            entity.setLinks(linkEntities);
            linkEntities.forEach(link -> link.setObservationArea(finalEntity));
        }
        return entity;
    }

    public CameraEntity getDefaultCamera(ObservationAreaDto dto, ObservationAreaEntity observationAreaEntity) {
        if (dto.getSaeStreamKey() == null || dto.getSaeStreamKey().isBlank()) {
            return null;
        }
        return new CameraEntity(dto.getSaeStreamKey());
    }
}
