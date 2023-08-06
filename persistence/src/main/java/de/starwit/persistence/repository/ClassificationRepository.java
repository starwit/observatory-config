package de.starwit.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.starwit.persistence.entity.ClassificationEntity;

/**
 * Classification Repository class
 */
@Repository
public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Long> {

}
