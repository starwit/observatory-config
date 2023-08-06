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

import de.starwit.persistence.entity.ParkingAreaEntity;
import de.starwit.service.impl.ParkingAreaService;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * ParkingArea RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/parkingarea")
public class ParkingAreaController {

    static final Logger LOG = LoggerFactory.getLogger(ParkingAreaController.class);

    @Autowired
    private ParkingAreaService parkingareaService;

    @Operation(summary = "Get all parkingarea")
    @GetMapping
    public List<ParkingAreaEntity> findAll() {
        return this.parkingareaService.findAll();
    }


    @Operation(summary = "Get parkingarea with id")
    @GetMapping(value = "/{id}")
    public ParkingAreaEntity findById(@PathVariable("id") Long id) {
        return this.parkingareaService.findById(id);
    }

    @Operation(summary = "Create parkingarea")
    @PostMapping
    public ParkingAreaEntity save(@Valid @RequestBody ParkingAreaEntity entity) {
        return update(entity);
    }

    @Operation(summary = "Update parkingarea")
    @PutMapping
    public ParkingAreaEntity update(@Valid @RequestBody ParkingAreaEntity entity) {
        return parkingareaService.saveOrUpdate(entity);
    }

    @Operation(summary = "Delete parkingarea")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        parkingareaService.delete(id);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("ParkingArea not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.parkingarea.notfound", "ParkingArea not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
