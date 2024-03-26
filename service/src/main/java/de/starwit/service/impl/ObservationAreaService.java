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
        ImageEntity image = new ImageEntity();
        if (dto.getId() == null) {
            entity = mapper.convertToEntity(dto);
            if (entity != null) {
                entity = observationareaRepository.saveAndFlush(entity);
                List<CameraEntity> cameras = mapper.getDefaultCameras(dto, entity);
                cameras = cameras == null ? null : cameraRepository.saveAllAndFlush(cameras);
            }
        } else {
            entity = this.findById(dto.getId());
            entity = mapper.convertToEntity(dto, entity);
            mapAndSaveCameras(dto.getSaeIds(), entity);
            if (entity.getImage() != null) {
                long imageId = entity.getImage().getId();
                image = imageRepository.getReferenceById(imageId);
            }
        }
        image.setObservationArea(entity);
        image.setName(dto.getName());
        image = imageRepository.saveAndFlush(image);
        entity = observationareaRepository.saveAndFlush(entity);
        observationareaRepository.refresh(entity);
        return mapper.convertToDto(entity);
    }

    private void mapAndSaveCameras(List<String> addedCameras, ObservationAreaEntity entity) {
        List<CameraEntity> newCameraList = new ArrayList<>();
        if (addedCameras != null && !addedCameras.isEmpty()){
            for (String saeId : addedCameras) {
                List<CameraEntity> existingCamera = cameraRepository.findBySaeIdAndObservationArea(saeId, entity);
                if (existingCamera != null && !existingCamera.isEmpty()) {
                    existingCamera.get(0).setObservationArea(entity);
                    newCameraList.addAll(existingCamera);
                } else {
                    CameraEntity camera = new CameraEntity(saeId, entity);
                    newCameraList.add(camera);
                }
            }
        }
        List<CameraEntity> toBeRemoved = cameraRepository.findByObservationArea(entity);
        toBeRemoved.removeAll(newCameraList);
        cameraRepository.deleteAll(toBeRemoved);
        cameraRepository.saveAllAndFlush(newCameraList);
    }
}