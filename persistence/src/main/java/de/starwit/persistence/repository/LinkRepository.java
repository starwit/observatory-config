package de.starwit.persistence.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import de.starwit.persistence.entity.LinkEntity;

/**
 * LinkRepository class
 */
public interface LinkRepository extends JpaRepository<LinkEntity, Long> {

    Set<LinkEntity> findByObservationAreaId(Long observationAreaId);
}
