package de.starwit.service.mapper;

import java.util.ArrayList;
import java.util.List;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ParkingAreaEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.service.dto.ParkingAreaDto;

public class ParkingAreaMapper implements CustomMapper<ParkingAreaEntity, ParkingAreaDto> {

    @Override
    public ParkingAreaDto convertToDto(ParkingAreaEntity entity) {
        if (entity == null) {
            return null;
        }
        ParkingAreaDto dto = new ParkingAreaDto();
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
    public ParkingAreaEntity convertToEntity(ParkingAreaDto dto) {
        if (dto == null) {
            return null;
        }
        ParkingAreaEntity entity = new ParkingAreaEntity();
        return convertToEntity(dto, entity);
    }

    public ParkingAreaEntity convertToEntity(ParkingAreaDto dto, ParkingAreaEntity entity) {
        if (dto == null) {
            return null;
        }
        if (entity == null) {
            entity = new ParkingAreaEntity();
        }
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCenterlatitude(dto.getCenterlatitude());
        entity.setCenterlongitude(dto.getCenterlongitude());
        return entity;
    }

    public ParkingAreaEntity addDefaultParkingConfig(ParkingAreaDto dto, ParkingAreaEntity entity) {
        if (dto == null) {
            return entity;
        }
        List<ParkingConfigEntity> pcs = new ArrayList<>();
        pcs.add(getDefaultParkingConfig(dto, entity));
        entity.setParkingConfig(pcs);
        return entity;
    }

    public ParkingConfigEntity getDefaultParkingConfig(ParkingAreaDto dto, ParkingAreaEntity entity) {
        ParkingConfigEntity pc = new ParkingConfigEntity();
        pc.setName(dto.getName());
        pc.setParkingArea(entity);
        return pc;
    }

    public ParkingConfigEntity addDefaultImage(ParkingAreaDto dto, ParkingConfigEntity parkingConfigEntity) {
        List<ImageEntity> images = new ArrayList<>();
        images.add(getDefaultImage(dto, parkingConfigEntity));
        parkingConfigEntity.setImage(images);
        return parkingConfigEntity;
    }

    public List<CameraEntity> getDefaultCameras(ParkingAreaDto dto, ImageEntity image) {
        if (dto.getSaeIds() == null) {
            return null;
        }
        List<CameraEntity> cameras = new ArrayList<>();
        dto.getSaeIds().forEach(saeId -> {
            cameras.add(new CameraEntity(saeId, image));
        });
        return cameras;
    }

    public ImageEntity getDefaultImage(ParkingAreaDto dto, ParkingConfigEntity parkingConfigEntity) {
        if (parkingConfigEntity == null) {
            return null;
        }
        ImageEntity image = new ImageEntity();
        mapImageData(dto, parkingConfigEntity, image);
        return image;
    }

    public ImageEntity mapImageData(ParkingAreaDto dto, ParkingConfigEntity parkingConfigEntity, ImageEntity image) {
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
