package de.starwit.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.persistence.repository.CameraRepository;
import de.starwit.persistence.repository.ImageRepository;
import de.starwit.persistence.repository.ObservationAreaRepository;
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
                createDefault(dto, entity);
                entity = observationareaRepository.saveAndFlush(entity);
            }
        } else {
            entity = this.findById(dto.getId());
            entity = mapper.convertToEntity(dto, entity);
            entity = observationareaRepository.saveAndFlush(entity);
            if (entity.getImage() == null) {
                ImageEntity image = mapper.getDefaultImage(dto, entity);
                image = image == null ? null : imageRepository.saveAndFlush(image);
                List<CameraEntity> cameras = mapper.getDefaultCameras(dto, image);
                cameras = cameras == null ? null : cameraRepository.saveAllAndFlush(cameras);
                imageRepository.refresh(image);
            } else {
                long imageId = entity.getImage().getId();
                ImageEntity image = imageRepository.getReferenceById(imageId);
                image = mapper.mapImageData(dto, entity, image);
                image = image == null ? null : imageRepository.saveAndFlush(image);
                mapAndSaveCameras(dto.getSaeIds(), image);
                imageRepository.refresh(image);
            }
            entity = observationareaRepository.saveAndFlush(entity);
        }
        observationareaRepository.refresh(entity);
        return mapper.convertToDto(entity);
    }

    private ObservationAreaEntity createDefault(ObservationAreaDto dto, ObservationAreaEntity entity) {
        entity = observationareaRepository.saveAndFlush(entity);
        ImageEntity image = mapper.getDefaultImage(dto, entity);
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
