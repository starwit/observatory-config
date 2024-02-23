package de.starwit.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import de.starwit.persistence.entity.CameraEntity;

/**
 * Camera Repository class
 */
@Repository
public interface CameraRepository extends JpaRepository<CameraEntity, Long> {

    @Query("SELECT e FROM CameraEntity e WHERE NOT EXISTS (SELECT r FROM e.image r)")
    public List<CameraEntity> findAllWithoutImage();

    @Query("SELECT e FROM CameraEntity e WHERE NOT EXISTS (SELECT r FROM e.image r WHERE r.id <> ?1)")
    public List<CameraEntity> findAllWithoutOtherImage(Long id);

    @Query("SELECT e.id FROM CameraEntity e WHERE e.image is null")
    public List<Long> findAllWithEmptyImage();

    public List<CameraEntity> findBySaeId(String saeId);
}
