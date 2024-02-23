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
import de.starwit.persistence.repository.ParkingAreaRepository;
import de.starwit.persistence.repository.ParkingConfigRepository;
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
    private CameraRepository cameraRepository;

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

    @Override
    public ParkingAreaEntity saveOrUpdate(ParkingAreaEntity entity) {

        if (entity.getId() != null) {
            ParkingAreaEntity entityPrev = this.findById(entity.getId());
            entityPrev.setName(entity.getName());
            if (entity.getParkingConfig() != null || !entity.getParkingConfig().isEmpty()) {
                ParkingConfigEntity pc = entity.getParkingConfig().get(0);
                pc.setName(entity.getName());
                entity.setSelectedProdConfig(pc);
            }
        }

        entity = this.getRepository().saveAndFlush(entity);
        ParkingConfigEntity p = new ParkingConfigEntity();
        if (entity.getParkingConfig() == null || entity.getParkingConfig().isEmpty()) {
            p.setName(entity.getName());
            p.setParkingArea(entity);
            entity.setSelectedProdConfig(p);
            p = parkingconfigRepository.saveAndFlush(p);
        } else {
            if (entity.getSelectedProdConfig() != null) {
                p = entity.getSelectedProdConfig();
                p.setName(entity.getName());
                if (p.getImage() != null && !p.getImage().isEmpty()) {
                    p.getImage().get(0).setName(entity.getName());
                    saveCamera(p.getImage().get(0));
                }
                p = parkingconfigRepository.saveAndFlush(p);
            }
        }

        return this.getRepository().getReferenceById(entity.getId());
    }

    private void saveCamera(ImageEntity imageEntity) {
        List<CameraEntity> addedCameras = imageEntity.getCamera();
        List<CameraEntity> newCameraList = new ArrayList<>();
        List<CameraEntity> existingCameras = cameraRepository.findAll();
        if (addedCameras != null && !addedCameras.isEmpty()){
            for (CameraEntity camera : addedCameras) {
                List<CameraEntity> existingCamera = cameraRepository.findBySaeId(camera.getSaeId());
                if (existingCamera != null && !existingCamera.isEmpty()) {
                    newCameraList.add(existingCamera.get(0));
                } else {
                    newCameraList.add(camera);
                }
            }
        }

        List<Long> cameraIds = cameraRepository.findAllWithEmptyImage();
    }
}
