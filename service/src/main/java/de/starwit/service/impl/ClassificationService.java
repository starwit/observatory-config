package de.starwit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.repository.ClassificationRepository;

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

    @Override
    public List<ClassificationEntity> findAll() {
        return this.getRepository().findAllByOrderByIdAsc();
    }

    public ClassificationEntity findByName(String cls) {
        return classificationRepository.findOneByName(cls);
    }

}
