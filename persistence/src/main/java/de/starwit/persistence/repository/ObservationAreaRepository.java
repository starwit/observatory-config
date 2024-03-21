package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import de.starwit.persistence.entity.ObservationAreaEntity;

/**
 * ObservationArea Repository class
 */
public interface ObservationAreaRepository extends CustomRepository<ObservationAreaEntity, Long> {

    public List<ObservationAreaEntity> findAllByOrderByNameAsc();
}
