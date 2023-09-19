package de.starwit.persistence.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import de.starwit.persistence.entity.ClassificationEntity;

/**
 * Classification Repository class
 */
public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Long> {

    Set<ClassificationEntity> findByName(String name);

}
