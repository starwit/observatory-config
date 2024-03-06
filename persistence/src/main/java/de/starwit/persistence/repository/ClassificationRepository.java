package de.starwit.persistence.repository;

import java.util.List;

import de.starwit.persistence.entity.ClassificationEntity;

/**
 * Classification Repository class
 */
public interface ClassificationRepository extends CustomRepository<ClassificationEntity, Long> {

    ClassificationEntity findOneByName(String name);

    List<ClassificationEntity> findAllByOrderByIdAsc();

}
