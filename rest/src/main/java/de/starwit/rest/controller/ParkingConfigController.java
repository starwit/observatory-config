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

import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.service.impl.ParkingConfigService;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * ParkingConfig RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/parkingconfig")
public class ParkingConfigController {

    static final Logger LOG = LoggerFactory.getLogger(ParkingConfigController.class);

    @Autowired
    private ParkingConfigService parkingconfigService;

    @Operation(summary = "Get all parkingconfig")
    @GetMapping
    public List<ParkingConfigEntity> findAll() {
        return this.parkingconfigService.findAll();
    }

    @Operation(summary = "Get all parkingconfig without parkingArea")
    @GetMapping(value = "/find-without-parkingArea")
    public List<ParkingConfigEntity> findAllWithoutParkingArea() {
        return parkingconfigService.findAllWithoutParkingArea();
    }

    @Operation(summary = "Get all parkingconfig without other parkingArea")
    @GetMapping(value = "/find-without-other-parkingArea/{id}")
    public List<ParkingConfigEntity> findAllWithoutOtherParkingArea(@PathVariable("id") Long id) {
        return parkingconfigService.findAllWithoutOtherParkingArea(id);
    }

    @Operation(summary = "Get parkingconfig with id")
    @GetMapping(value = "/{id}")
    public ParkingConfigEntity findById(@PathVariable("id") Long id) {
        return this.parkingconfigService.findById(id);
    }

    @Operation(summary = "Create parkingconfig")
    @PostMapping
    public ParkingConfigEntity save(@Valid @RequestBody ParkingConfigEntity entity) {
        return update(entity);
    }

    @Operation(summary = "Update parkingconfig")
    @PutMapping
    public ParkingConfigEntity update(@Valid @RequestBody ParkingConfigEntity entity) {
        return parkingconfigService.saveOrUpdate(entity);
    }

    @Operation(summary = "Delete parkingconfig")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        parkingconfigService.delete(id);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("ParkingConfig not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.parkingconfig.notfound", "ParkingConfig not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
