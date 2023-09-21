package de.starwit.service.impl;

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.repository.ClassificationRepository;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Set<ClassificationEntity> findByName(String cls) {
        return classificationRepository.findByName(cls);
    }

}
