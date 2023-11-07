package de.starwit.service.impl;

import java.util.List;
import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Point Service class
 *
 */
@Service
public class PointService implements ServiceInterface<PointEntity, PointRepository> {

    @Autowired
    private PointRepository pointRepository;

    @Override
    public PointRepository getRepository() {
        return pointRepository;
    }

    public List<PointEntity> findAllWithoutPolygon() {
        return pointRepository.findAllWithoutPolygon();
    }

    public List<PointEntity> findAllWithoutOtherPolygon(Long id) {
        return pointRepository.findAllWithoutOtherPolygon(id);
    }

    public PointEntity saveAndFlush(PointEntity pointEntity) {
        return pointRepository.saveAndFlush(pointEntity);
    }

    public List<PointEntity> saveAllAndFlush(List<PointEntity> pointEntities) {
        return pointRepository.saveAllAndFlush(pointEntities);
    }
}
