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
                image.getCamera().forEach(camera -> {cameras.add(camera.getSaeId());});
                dto.setSaeIds(cameras);
                dto.setDegreeperpixelx(image.getDegreeperpixelx());
                dto.setDegreeperpixely(image.getDegreeperpixely());
                dto.setGeoReferenced(image.getGeoReferenced());
                dto.setTopleftlatitude(image.getTopleftlatitude());
                dto.setCenterlongitude(image.getTopleftlongitude());
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
        pcs.add(getDefaultParkingConfig(dto));
        entity.setParkingConfig(pcs);
        return entity;
    }

    public ParkingConfigEntity getDefaultParkingConfig(ParkingAreaDto dto) {
        List<ImageEntity> images = getDefaultImages(dto);

        ParkingConfigEntity pc = new ParkingConfigEntity();
        pc.setName(dto.getName());
        pc.setImage(images);
        return pc;
    }

    public List<ImageEntity> getDefaultImages(ParkingAreaDto dto) {
        ImageEntity image = new ImageEntity();
        image = mapImageData(dto, image);

        List<CameraEntity> cameras = new ArrayList<>();
        if (dto.getSaeIds() != null){
            dto.getSaeIds().forEach(saeId -> {cameras.add(new CameraEntity(saeId));});
        }
        image.setCamera(cameras);
        List<ImageEntity> images = new ArrayList<>();
        images.add(image);
        return images;
    }

    public ImageEntity mapImageData(ParkingAreaDto dto, ImageEntity image) {
        if (image == null) {
            return null;
        }
        image.setDegreeperpixelx(dto.getDegreeperpixelx());
        image.setDegreeperpixely(dto.getDegreeperpixely());
        image.setGeoReferenced(dto.getGeoReferenced());
        image.setName(dto.getName());
        return image;
    }
}
