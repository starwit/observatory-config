package de.starwit.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import de.starwit.persistence.entity.ObservationAreaEntity;

/**
 * ObservationArea Repository class
 */
public interface ObservationAreaRepository extends CustomRepository<ObservationAreaEntity, Long> {

    @Query("SELECT e FROM ObservationAreaEntity e WHERE NOT EXISTS (SELECT r FROM e.selectedTestConfig r)")
    public List<ObservationAreaEntity> findAllWithoutSelectedTestConfig();

    @Query("SELECT e FROM ObservationAreaEntity e WHERE NOT EXISTS (SELECT r FROM e.selectedTestConfig r WHERE r.id <> ?1)")
    public List<ObservationAreaEntity> findAllWithoutOtherSelectedTestConfig(Long id);

    @Query("SELECT e FROM ObservationAreaEntity e WHERE NOT EXISTS (SELECT r FROM e.selectedProdConfig r)")
    public List<ObservationAreaEntity> findAllWithoutSelectedProdConfig();

    @Query("SELECT e FROM ObservationAreaEntity e WHERE NOT EXISTS (SELECT r FROM e.selectedProdConfig r WHERE r.id <> ?1)")
    public List<ObservationAreaEntity> findAllWithoutOtherSelectedProdConfig(Long id);

    public List<ObservationAreaEntity> findAllByOrderByNameAsc();
}
