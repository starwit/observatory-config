package de.starwit.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.persistence.repository.CameraRepository;
import de.starwit.persistence.repository.ImageRepository;
import de.starwit.persistence.repository.ObservationAreaRepository;
import de.starwit.persistence.repository.ParkingConfigRepository;
import de.starwit.service.dto.ObservationAreaDto;
import de.starwit.service.mapper.ObservationAreaMapper;

/**
 * 
 * ObservationArea Service class
 *
 */
@Service
public class ObservationAreaService implements ServiceInterface<ObservationAreaEntity, ObservationAreaRepository> {

    @Autowired
    private ObservationAreaRepository observationareaRepository;

    @Autowired
    private ParkingConfigRepository parkingconfigRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CameraRepository cameraRepository;

    private ObservationAreaMapper mapper = new ObservationAreaMapper();

    @Override
    public ObservationAreaRepository getRepository() {
        return observationareaRepository;
    }

    @Override
    public List<ObservationAreaEntity> findAll() {
        return this.getRepository().findAllByOrderByNameAsc();
    }

    public List<ObservationAreaEntity> findAllWithoutSelectedTestConfig() {
        return observationareaRepository.findAllWithoutSelectedTestConfig();
    }

    public List<ObservationAreaEntity> findAllWithoutOtherSelectedTestConfig(Long id) {
        return observationareaRepository.findAllWithoutOtherSelectedTestConfig(id);
    }

    public List<ObservationAreaEntity> findAllWithoutSelectedProdConfig() {
        return observationareaRepository.findAllWithoutSelectedProdConfig();
    }

    public List<ObservationAreaEntity> findAllWithoutOtherSelectedProdConfig(Long id) {
        return observationareaRepository.findAllWithoutOtherSelectedProdConfig(id);
    }

    @Override
    public void delete(Long id) throws NotificationException {
            this.getRepository().deleteById(id);
    }

    public ObservationAreaDto saveOrUpdateDto(ObservationAreaDto dto){
        if (dto == null) {
            return null;
        }
        ObservationAreaEntity entity = null;
        if (dto.getId() == null) {
            entity = mapper.convertToEntity(dto);
            if (entity != null) {
                entity = observationareaRepository.saveAndFlush(entity);
                entity = createDefaultParkingConfig(dto, entity);
            }
        } else {
            entity = this.findById(dto.getId());
            entity = mapper.convertToEntity(dto, entity);
            if (dto.getSelectedProdConfigId() == null) {
                entity = createDefaultParkingConfig(dto, entity);
            } else {
                long pcId = dto.getSelectedProdConfigId();
                ParkingConfigEntity pc = parkingconfigRepository.getReferenceById(pcId);
                pc.setName(dto.getName());
                pc.setObservationArea(entity);
                entity.setSelectedProdConfig(pc);
                entity = observationareaRepository.saveAndFlush(entity);
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
                entity = observationareaRepository.saveAndFlush(entity);
            }
        }
        observationareaRepository.refresh(entity);
        return mapper.convertToDto(entity);
    }

    private ObservationAreaEntity createDefaultParkingConfig(ObservationAreaDto dto, ObservationAreaEntity entity) {
        entity = mapper.addDefaultParkingConfig(dto, entity);
        entity.setSelectedProdConfig(entity.getParkingConfig().get(0));
        entity = observationareaRepository.saveAndFlush(entity);
        ImageEntity image = mapper.getDefaultImage(dto, entity.getSelectedProdConfig());
        image = image == null ? null : imageRepository.saveAndFlush(image);
        List<CameraEntity> cameras = mapper.getDefaultCameras(dto, image);
        cameras = cameras == null ? null : cameraRepository.saveAllAndFlush(cameras);
        imageRepository.refresh(image);
        return entity;
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
        List<CameraEntity> toBeRemoved = cameraRepository.findByImage(image);
        toBeRemoved.removeAll(newCameraList);
        cameraRepository.deleteAll(toBeRemoved);
        cameraRepository.saveAllAndFlush(newCameraList);
    }
}
