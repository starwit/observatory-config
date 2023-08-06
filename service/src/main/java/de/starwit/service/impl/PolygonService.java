package de.starwit.service.impl;
import java.util.List;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.PolygonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.repository.PointRepository;

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

    @Override
    public PolygonEntity saveOrUpdate(PolygonEntity entity) {

        Set<PointEntity> pointToSave = entity.getPoint();

        if (entity.getId() != null) {
            PolygonEntity entityPrev = this.findById(entity.getId());
            for (PointEntity item : entityPrev.getPoint()) {
                PointEntity existingItem = pointRepository.getById(item.getId());
                existingItem.setPolygon(null);
                this.pointRepository.save(existingItem);
            }
        }

        entity.setPoint(null);
        entity = this.getRepository().save(entity);
        this.getRepository().flush();

        if (pointToSave != null && !pointToSave.isEmpty()) {
            for (PointEntity item : pointToSave) {
                PointEntity newItem = pointRepository.getById(item.getId());
                newItem.setPolygon(entity);
                pointRepository.save(newItem);
            }
        }
        return this.getRepository().getById(entity.getId());
    }
}
