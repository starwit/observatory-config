package de.starwit.persistence.repository;

import org.springframework.stereotype.Repository;

import de.starwit.persistence.entity.DetectionEntity;

@Repository
public interface DetectionRepository extends CustomRepository<DetectionEntity, Long>{
    
}
