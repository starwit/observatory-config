package de.starwit.service.impl;
import java.util.List;
import java.util.stream.Collectors;

import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.PointRepository;
import jakarta.transaction.Transactional;
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

    public List<PointEntity> saveAll(List<PointEntity> pointEntities){
        return pointEntities.stream().map(this::save).collect(Collectors.toList());
    }

    public PointEntity save(PointEntity pointEntity){
        return this.pointRepository.save(pointEntity);
    }

    public PointEntity addPolygonToPoint(PointEntity pointEntity, PolygonEntity polygonEntity){
        pointEntity.setPolygon(polygonEntity);
        return this.save(pointEntity);
    }

    @Transactional
    public void deleteAllByPolygon(PolygonEntity polygonEntity){
        this.pointRepository.deleteAllByPolygon(polygonEntity);
    }

}
