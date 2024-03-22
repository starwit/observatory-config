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

import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.service.impl.PolygonService;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Polygon RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/polygon")
public class PolygonController {

    static final Logger LOG = LoggerFactory.getLogger(PolygonController.class);

    @Autowired
    private PolygonService polygonService;

    @Operation(summary = "Get all polygon")
    @GetMapping
    public List<PolygonEntity> findAll() {
        return this.polygonService.findAll();
    }

    @Operation(summary = "Get polygon with id")
    @GetMapping(value = "/{id}")
    public PolygonEntity findById(@PathVariable("id") Long id) {
        return this.polygonService.findById(id);
    }

    @Operation(summary = "Create polygon")
    @PostMapping
    public PolygonEntity save(@Valid @RequestBody PolygonEntity entity) {
        return update(entity);
    }

    @Operation(summary = "Update polygon")
    @PutMapping
    public PolygonEntity update(@Valid @RequestBody PolygonEntity entity) {
        return polygonService.saveOrUpdate(entity);
    }

    @Operation(summary = "Delete polygon")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        polygonService.delete(id);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("Polygon not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.polygon.notfound", "Polygon not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
