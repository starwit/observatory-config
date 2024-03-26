package de.starwit.service.mapper;

import java.util.ArrayList;
import java.util.List;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.service.dto.ObservationAreaDto;

public class ObservationAreaMapper implements CustomMapper<ObservationAreaEntity, ObservationAreaDto> {

    @Override
    public ObservationAreaDto convertToDto(ObservationAreaEntity entity) {
        if (entity == null) {
            return null;
        }
        ObservationAreaDto dto = new ObservationAreaDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCenterlatitude(entity.getCenterlatitude());
        dto.setCenterlongitude(entity.getCenterlongitude());
        List<String> cameras = new ArrayList<>();
        if(entity.getCamera() != null && !entity.getCamera().isEmpty()) {
            entity.getCamera().forEach(camera -> {cameras.add(camera.getSaeId());});
            dto.setSaeIds(cameras);
        }
        dto.setDegreeperpixelx(entity.getDegreeperpixelx());
        dto.setDegreeperpixely(entity.getDegreeperpixely());
        dto.setGeoReferenced(entity.getGeoReferenced());
        dto.setTopleftlatitude(entity.getTopleftlatitude());
        dto.setTopleftlongitude(entity.getTopleftlongitude());
        dto.setProcessingEnabled(entity.getProcessingEnabled());
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
        return entity;
    }

    public List<CameraEntity> getDefaultCameras(ObservationAreaDto dto, ObservationAreaEntity observationAreaEntity) {
        if (dto.getSaeIds() == null) {
            return null;
        }
        List<CameraEntity> cameras = new ArrayList<>();
        dto.getSaeIds().forEach(saeId -> {
            cameras.add(new CameraEntity(saeId, observationAreaEntity));
        });
        return cameras;
    }
}
