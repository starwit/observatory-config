package de.starwit.service.mapper;

import java.util.ArrayList;
import java.util.List;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
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
        if (entity.getSelectedProdConfig() != null) {
            ParkingConfigEntity pc = entity.getSelectedProdConfig();
            dto.setSelectedProdConfigId(pc.getId());
            if (pc.getImage() != null && !pc.getImage().isEmpty()){
                ImageEntity image = pc.getImage().get(0);
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

    public ObservationAreaEntity addDefaultParkingConfig(ObservationAreaDto dto, ObservationAreaEntity entity) {
        if (dto == null) {
            return entity;
        }
        List<ParkingConfigEntity> pcs = new ArrayList<>();
        pcs.add(getDefaultParkingConfig(dto, entity));
        entity.setParkingConfig(pcs);
        return entity;
    }

    public ParkingConfigEntity getDefaultParkingConfig(ObservationAreaDto dto, ObservationAreaEntity entity) {
        ParkingConfigEntity pc = new ParkingConfigEntity();
        pc.setName(dto.getName());
        pc.setObservationArea(entity);
        return pc;
    }

    public ParkingConfigEntity addDefaultImage(ObservationAreaDto dto, ParkingConfigEntity parkingConfigEntity) {
        List<ImageEntity> images = new ArrayList<>();
        images.add(getDefaultImage(dto, parkingConfigEntity));
        parkingConfigEntity.setImage(images);
        return parkingConfigEntity;
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

    public ImageEntity getDefaultImage(ObservationAreaDto dto, ParkingConfigEntity parkingConfigEntity) {
        if (parkingConfigEntity == null) {
            return null;
        }
        ImageEntity image = new ImageEntity();
        mapImageData(dto, parkingConfigEntity, image);
        return image;
    }

    public ImageEntity mapImageData(ObservationAreaDto dto, ParkingConfigEntity parkingConfigEntity, ImageEntity image) {
        image.setDegreeperpixelx(dto.getDegreeperpixelx());
        image.setDegreeperpixely(dto.getDegreeperpixely());
        image.setTopleftlatitude(dto.getTopleftlatitude());
        image.setTopleftlongitude(dto.getTopleftlongitude());
        image.setGeoReferenced(dto.getGeoReferenced());
        image.setParkingConfig(parkingConfigEntity);
        image.setName(dto.getName());
        return image;
    }
}
