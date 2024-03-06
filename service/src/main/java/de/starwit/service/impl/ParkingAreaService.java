package de.starwit.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ParkingAreaEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.persistence.repository.CameraRepository;
import de.starwit.persistence.repository.ImageRepository;
import de.starwit.persistence.repository.ParkingAreaRepository;
import de.starwit.persistence.repository.ParkingConfigRepository;
import de.starwit.service.dto.ParkingAreaDto;
import de.starwit.service.mapper.ParkingAreaMapper;

/**
 * 
 * ParkingArea Service class
 *
 */
@Service
public class ParkingAreaService implements ServiceInterface<ParkingAreaEntity, ParkingAreaRepository> {

    @Autowired
    private ParkingAreaRepository parkingareaRepository;

    @Autowired
    private ParkingConfigRepository parkingconfigRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CameraRepository cameraRepository;

    private ParkingAreaMapper mapper = new ParkingAreaMapper();

    @Override
    public ParkingAreaRepository getRepository() {
        return parkingareaRepository;
    }

    @Override
    public List<ParkingAreaEntity> findAll() {
        return this.getRepository().findAllByOrderByNameAsc();
    }

    public List<ParkingAreaEntity> findAllWithoutSelectedTestConfig() {
        return parkingareaRepository.findAllWithoutSelectedTestConfig();
    }

    public List<ParkingAreaEntity> findAllWithoutOtherSelectedTestConfig(Long id) {
        return parkingareaRepository.findAllWithoutOtherSelectedTestConfig(id);
    }

    public List<ParkingAreaEntity> findAllWithoutSelectedProdConfig() {
        return parkingareaRepository.findAllWithoutSelectedProdConfig();
    }

    public List<ParkingAreaEntity> findAllWithoutOtherSelectedProdConfig(Long id) {
        return parkingareaRepository.findAllWithoutOtherSelectedProdConfig(id);
    }

    @Override
    public void delete(Long id) throws NotificationException {
        if (id != 1) {
            this.getRepository().deleteById(id);
        }
    }

    public ParkingAreaDto saveOrUpdateDto(ParkingAreaDto dto){
        if (dto == null) {
            return null;
        }
        ParkingAreaEntity entity = null;
        if (dto.getId() == null) {
            entity = mapper.convertToEntity(dto);
            if (entity != null) {
                entity = parkingareaRepository.saveAndFlush(entity);
                entity = mapper.addDefaultParkingConfig(dto, entity);
                entity.setSelectedProdConfig(entity.getParkingConfig().get(0));
                entity = parkingareaRepository.saveAndFlush(entity);
                ImageEntity image = mapper.getDefaultImage(dto, entity.getSelectedProdConfig());
                image = image == null ? null : imageRepository.saveAndFlush(image);
                List<CameraEntity> cameras = mapper.getDefaultCameras(dto, image);
                cameras = cameras == null ? null : cameraRepository.saveAllAndFlush(cameras);
                imageRepository.refresh(image);
            }
        } else {
            entity = this.findById(dto.getId());
            entity = mapper.convertToEntity(dto, entity);
            if (dto.getSelectedProdConfigId() == null) {
                entity = mapper.addDefaultParkingConfig(dto, entity);
                entity.setSelectedProdConfig(entity.getParkingConfig().get(0));
                entity = parkingareaRepository.saveAndFlush(entity);
                ImageEntity image = mapper.getDefaultImage(dto, entity.getSelectedProdConfig());
                image = image == null ? null : imageRepository.saveAndFlush(image);
                List<CameraEntity> cameras = mapper.getDefaultCameras(dto, image);
                cameras = cameras == null ? null : cameraRepository.saveAllAndFlush(cameras);
                imageRepository.refresh(image);
            } else {
                long pcId = dto.getSelectedProdConfigId();
                ParkingConfigEntity pc = parkingconfigRepository.getReferenceById(pcId);
                pc.setName(dto.getName());
                pc.setParkingArea(entity);
                entity.setSelectedProdConfig(pc);
                entity = parkingareaRepository.saveAndFlush(entity);
                if (pc.getImage() == null 
                    || pc.getImage().isEmpty() 
                    || pc.getImage().get(0) == null 
                    || pc.getImage().get(0).getId() == null) {
                    ImageEntity image = mapper.getDefaultImage(dto, entity.getSelectedProdConfig());
                    image = image == null ? null : imageRepository.saveAndFlush(image);
                    List<CameraEntity> cameras = mapper.getDefaultCameras(dto, image);
                    cameras = cameras == null ? null : cameraRepository.saveAllAndFlush(cameras);
                    imageRepository.refresh(image);
                } else {
                    long imageId = pc.getImage().get(0).getId();
                    ImageEntity image = imageRepository.getReferenceById(imageId);
                    image = mapper.mapImageData(dto, pc, image);
                    image = image == null ? null : imageRepository.saveAndFlush(image);
                    mapAndSaveCameras(dto.getSaeIds(), image);
                    imageRepository.refresh(image);
                }
                parkingconfigRepository.refresh(pc);
                entity.setSelectedTestConfig(null);
                entity = parkingareaRepository.saveAndFlush(entity);
            }
        }
        parkingareaRepository.refresh(entity);
        return mapper.convertToDto(entity);
    }

    @Override
    public ParkingAreaEntity saveOrUpdate(ParkingAreaEntity entity) {

        ParkingAreaEntity newEntity = null;
        if (entity == null) {
            return null;
        }
        
        if (entity.getId() == null) {
            newEntity = createDefaultParkingConfigForArea(entity);
        } else {
            newEntity = this.findById(entity.getId());
            newEntity.setCenterlatitude(entity.getCenterlatitude());
            newEntity.setCenterlongitude(entity.getCenterlongitude());
            newEntity.setName(entity.getName());
            if (entity.getParkingConfig() == null || entity.getParkingConfig().isEmpty()){
                newEntity = createDefaultParkingConfigForArea(newEntity);
            }
            if (entity.getSelectedProdConfig() == null) {
                newEntity.getParkingConfig().get(0).setName(entity.getName());
                newEntity.setSelectedProdConfig(newEntity.getParkingConfig().get(0));
            }
        }
        newEntity = this.getRepository().saveAndFlush(newEntity);

        if(entity != null 
        && entity.getSelectedProdConfig() != null 
        && entity.getSelectedProdConfig().getImage() != null
        && !entity.getSelectedProdConfig().getImage().isEmpty()) {
            newEntity.getSelectedProdConfig().setName(entity.getName());
            ImageEntity image = entity.getSelectedProdConfig().getImage().get(0);
            saveImage(newEntity.getSelectedProdConfig(), image);
        }

        this.getRepository().saveAndFlush(newEntity);
        return getRepository().findById(newEntity.getId()).orElseThrow();
    }

    private ParkingAreaEntity createDefaultParkingConfigForArea(ParkingAreaEntity entity) {
        ParkingConfigEntity pc = new ParkingConfigEntity();
        pc.setName(entity.getName());
        pc = parkingconfigRepository.saveAndFlush(pc);
        pc.setParkingArea(entity);
        entity.setSelectedTestConfig(null);
        entity.setSelectedProdConfig(pc);
        List<ParkingConfigEntity> pcList = new ArrayList<>();
        pcList.add(pc);
        entity.setParkingConfig(pcList);
        return entity;
    }

    private void saveImage(ParkingConfigEntity pcEntity, ImageEntity image){
        ImageEntity newImage = imageRepository.findById(image.getId()).orElse(image);
        newImage.setName(pcEntity.getName());
        newImage.setDegreeperpixelx(image.getDegreeperpixelx());
        newImage.setDegreeperpixely(image.getDegreeperpixely());
        newImage.setGeoReferenced(image.getGeoReferenced());
        newImage.setTopleftlatitude(image.getTopleftlatitude());
        newImage.setTopleftlongitude(image.getTopleftlongitude());
        newImage.setParkingConfig(pcEntity);
        newImage = imageRepository.saveAndFlush(newImage);
    }

    private void mapAndSaveCameras(List<String> addedCameras, ImageEntity image) {
        List<CameraEntity> newCameraList = new ArrayList<>();
        if (addedCameras != null && !addedCameras.isEmpty()){
            for (String saeId : addedCameras) {
                List<CameraEntity> existingCamera = cameraRepository.findBySaeIdAndImage(saeId, image);
                if (existingCamera != null && !existingCamera.isEmpty()) {
                    existingCamera.get(0).setImage(image);
                    newCameraList.addAll(existingCamera);
                } else {
                    CameraEntity camera = new CameraEntity(saeId, image);
                    newCameraList.add(camera);
                }
            }
        }
        cameraRepository.saveAllAndFlush(newCameraList);
    }
}
