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
import jakarta.persistence.EntityNotFoundException;

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
        ParkingAreaEntity entity = new ParkingAreaEntity();
        entity = mapper.convertToEntity(dto);
        if (dto.getId() == null) {
            entity = mapper.addDefaultParkingConfig(dto, entity);
            if (entity != null) {
                entity = parkingareaRepository.saveAndFlush(entity);
                entity.setSelectedProdConfig(entity.getParkingConfig().get(0));
                entity = parkingareaRepository.save(entity);
            }
        } else {
            entity = this.findById(entity.getId());
            entity = mapper.convertToEntity(dto, entity);
            if (dto.getSelectedProdConfigId() == null) {
                ParkingConfigEntity pc = mapper.getDefaultParkingConfig(dto);
                pc.setParkingArea(entity);
                List<ParkingConfigEntity> pcs = new ArrayList<>();
                pcs.add(pc);
                entity.setParkingConfig(pcs);
                entity = parkingareaRepository.saveAndFlush(entity);
                entity.setSelectedProdConfig(entity.getParkingConfig().get(0));
                entity.setSelectedTestConfig(null);
                entity = parkingareaRepository.save(entity);
            } else {
                long pcId = dto.getSelectedProdConfigId();
                ParkingConfigEntity pc = parkingconfigRepository.getReferenceById(pcId);
                pc.setName(dto.getName());
                if (pc.getImage() == null 
                    || pc.getImage().isEmpty() 
                    || pc.getImage().get(0) == null 
                    || pc.getImage().get(0).getId() == null) {
                    pc.setImage(mapper.getDefaultImages(dto));
                } else {
                    long imageId = pc.getImage().get(0).getId();
                    ImageEntity imageEntity = imageRepository.getReferenceById(imageId);
                    imageEntity = mapper.mapImageData(dto, imageEntity);
                    imageEntity = mapCamera(dto.getSaeIds(), imageEntity);
                }
                List<ParkingConfigEntity> pcs = new ArrayList<>();
                pcs.add(pc);
                entity.setParkingConfig(pcs);
                entity.setSelectedTestConfig(null);
                entity = parkingareaRepository.save(entity);
            }
        }
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

    private ImageEntity mapCamera(List<String> addedCameras, ImageEntity image) {
        List<CameraEntity> newCameraList = new ArrayList<>();
        if (addedCameras != null && !addedCameras.isEmpty()){
            for (String saeId : addedCameras) {
                List<CameraEntity> existingCamera = cameraRepository.findBySaeId(saeId);
                if (existingCamera != null && !existingCamera.isEmpty()) {
                    existingCamera.get(0).setImage(image);
                    newCameraList.addAll(existingCamera);
                } else {
                    CameraEntity camera = new CameraEntity();
                    camera.setSaeId(saeId);
                    camera.setImage(image);
                    newCameraList.add(camera);
                }
            }
        }
        image.setCamera(newCameraList);
        return image;
    }
}
