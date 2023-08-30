package de.starwit.service.impl;

import java.util.List;

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.entity.ImageEntity;
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
    private PointService pointService;
    @Autowired
    private ClassificationService classificationService;
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
    public PolygonEntity saveOrUpdate(PolygonEntity polygonEntity) {

        Set<PointEntity> pointsToSave = polygonEntity.getPoints();
        Set<ClassificationEntity> classificationEntities = polygonEntity.getClassifications();

        if (polygonEntity.getId() != null) {
            PolygonEntity entityPrev = this.findById(polygonEntity.getId());
            for (PointEntity point : entityPrev.getPoints()) {
                PointEntity existingItem = pointRepository.getReferenceById(point.getId());
                existingItem.setPolygon(null);
                this.pointRepository.save(existingItem);
            }
        }

        polygonEntity.setPoints(null);
        polygonEntity = this.getRepository().save(polygonEntity);
        this.getRepository().flush();

        if (pointsToSave != null && !pointsToSave.isEmpty()) {
            for (PointEntity item : pointsToSave) {
                PointEntity newItem = pointService.save(item);
                pointService.addPolygonToPoint(newItem, polygonEntity);
            }
        }

        if (classificationEntities != null && !classificationEntities.isEmpty()) {
            for (ClassificationEntity classification : classificationEntities) {
                ClassificationEntity newClassification = classificationService.saveOrUpdate(classification);
                classificationService.assignPolygonToClassification(polygonEntity, newClassification);
            }
        }
        return this.getRepository().getReferenceById(polygonEntity.getId());
    }

    PolygonEntity assignPolygonToImage(PolygonEntity polygonEntity, ImageEntity imageEntity){
        polygonEntity.setImage(imageEntity);
        return polygonRepository.save(polygonEntity);
    }

}
