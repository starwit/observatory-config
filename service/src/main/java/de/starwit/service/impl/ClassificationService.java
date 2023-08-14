package de.starwit.service.impl;
import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.ClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


/**
 * 
 * Classification Service class
 *
 */
@Service
public class ClassificationService implements ServiceInterface<ClassificationEntity, ClassificationRepository> {

    @Autowired
    private ClassificationRepository classificationRepository;

    @Override
    public ClassificationRepository getRepository() {
        return classificationRepository;
    }

    public ClassificationEntity assignPolygonToClassification(PolygonEntity polygonEntity, ClassificationEntity classificationEntity){
        Set<PolygonEntity> classificationEntitySet = classificationEntity.getPolygon();
        classificationEntitySet.add(polygonEntity);
        classificationEntity.setPolygon(classificationEntitySet);
        return this.saveOrUpdate(classificationEntity);
    }

}
