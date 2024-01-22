package de.starwit.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.PointRepository;
import de.starwit.persistence.repository.PolygonRepository;

/**
 * 
 * Polygon Service class
 *
 */
@Service
public class PolygonService implements ServiceInterface<PolygonEntity, PolygonRepository> {

    @Autowired
    private PolygonRepository polygonRepository;

    @Autowired
    private PointRepository pointRepository;

    @Override
    public PolygonRepository getRepository() {
        return polygonRepository;
    }

    public List<PolygonEntity> findAllWithoutImage() {
        return polygonRepository.findAllWithoutImage();
    }

    public List<PolygonEntity> findAllWithoutOtherImage(Long id) {
        return polygonRepository.findAllWithoutOtherImage(id);
    }

    public PolygonEntity saveAndFlush(PolygonEntity polygonEntity) {
        return polygonRepository.saveAndFlush(polygonEntity);
    }

    public void deleteAll(Set<PolygonEntity> entities) {
        for (PolygonEntity polygonEntity : entities) {
            pointRepository.deleteAll(polygonEntity.getPoint());
        }
        polygonRepository.deleteAll(entities);
    }
}
