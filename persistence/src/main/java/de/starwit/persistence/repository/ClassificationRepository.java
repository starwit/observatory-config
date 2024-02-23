package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.starwit.persistence.entity.ClassificationEntity;

/**
 * Classification Repository class
 */
public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Long> {

    ClassificationEntity findOneByName(String name);

    List<ClassificationEntity> findAllByOrderByIdAsc();

}
