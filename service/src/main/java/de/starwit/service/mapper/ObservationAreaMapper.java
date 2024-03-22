package de.starwit.service.mapper;

import java.util.ArrayList;
import java.util.List;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ImageEntity;
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
        if (entity.getImage() != null){
            ImageEntity image = entity.getImage();
            List<String> cameras = new ArrayList<>();
            if(image.getCamera() != null && !image.getCamera().isEmpty()) {
                image.getCamera().forEach(camera -> {cameras.add(camera.getSaeId());});
                dto.setSaeIds(cameras);
            }
            dto.setDegreeperpixelx(image.getDegreeperpixelx());
            dto.setDegreeperpixely(image.getDegreeperpixely());
            dto.setGeoReferenced(image.getGeoReferenced());
            dto.setTopleftlatitude(image.getTopleftlatitude());
            dto.setTopleftlongitude(image.getTopleftlongitude());
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
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCenterlatitude(dto.getCenterlatitude());
        entity.setCenterlongitude(dto.getCenterlongitude());
        return entity;
    }

    public ObservationAreaEntity addDefaultImage(ObservationAreaDto dto, ObservationAreaEntity observationAreaEntity) {
        observationAreaEntity.setImage(getDefaultImage(dto, observationAreaEntity));
        return observationAreaEntity;
    }

    public List<CameraEntity> getDefaultCameras(ObservationAreaDto dto, ImageEntity image) {
        if (dto.getSaeIds() == null) {
            return null;
        }
        List<CameraEntity> cameras = new ArrayList<>();
        dto.getSaeIds().forEach(saeId -> {
            cameras.add(new CameraEntity(saeId, image));
        });
        return cameras;
    }

    public ImageEntity getDefaultImage(ObservationAreaDto dto, ObservationAreaEntity observationAreaEntity) {
        if (observationAreaEntity == null) {
            return null;
        }
        ImageEntity image = new ImageEntity();
        mapImageData(dto, observationAreaEntity, image);
        return image;
    }

    public ImageEntity mapImageData(ObservationAreaDto dto, ObservationAreaEntity observationAreaEntity, ImageEntity image) {
        image.setDegreeperpixelx(dto.getDegreeperpixelx());
        image.setDegreeperpixely(dto.getDegreeperpixely());
        image.setTopleftlatitude(dto.getTopleftlatitude());
        image.setTopleftlongitude(dto.getTopleftlongitude());
        image.setGeoReferenced(dto.getGeoReferenced());
        image.setObservationArea(observationAreaEntity);
        image.setName(dto.getName());
        return image;
    }
}
