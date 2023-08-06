package de.starwit.rest.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.starwit.persistence.entity.PointEntity;
import de.starwit.service.impl.PointService;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Point RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/point")
public class PointController {

    static final Logger LOG = LoggerFactory.getLogger(PointController.class);

    @Autowired
    private PointService pointService;

    @Operation(summary = "Get all point")
    @GetMapping
    public List<PointEntity> findAll() {
        return this.pointService.findAll();
    }

    @Operation(summary = "Get all point without polygon")
    @GetMapping(value = "/find-without-polygon")
    public List<PointEntity> findAllWithoutPolygon() {
        return pointService.findAllWithoutPolygon();
    }

    @Operation(summary = "Get all point without other polygon")
    @GetMapping(value = "/find-without-other-polygon/{id}")
    public List<PointEntity> findAllWithoutOtherPolygon(@PathVariable("id") Long id) {
        return pointService.findAllWithoutOtherPolygon(id);
    }

    @Operation(summary = "Get point with id")
    @GetMapping(value = "/{id}")
    public PointEntity findById(@PathVariable("id") Long id) {
        return this.pointService.findById(id);
    }

    @Operation(summary = "Create point")
    @PostMapping
    public PointEntity save(@Valid @RequestBody PointEntity entity) {
        return update(entity);
    }

    @Operation(summary = "Update point")
    @PutMapping
    public PointEntity update(@Valid @RequestBody PointEntity entity) {
        return pointService.saveOrUpdate(entity);
    }

    @Operation(summary = "Delete point")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        pointService.delete(id);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("Point not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.point.notfound", "Point not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
