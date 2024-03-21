package de.starwit.rest.controller;

import java.util.List;

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
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import de.starwit.service.impl.ParkingConfigService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

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

    @Operation(summary = "Get all parkingconfig without observationArea")
    @GetMapping(value = "/find-without-observationArea")
    public List<ParkingConfigEntity> findAllWithoutObservationArea() {
        return parkingconfigService.findAllWithoutObservationArea();
    }

    @Operation(summary = "Get all parkingconfig without other observationArea")
    @GetMapping(value = "/find-without-other-observationArea/{id}")
    public List<ParkingConfigEntity> findAllWithoutOtherObservationArea(@PathVariable("id") Long id) {
        return parkingconfigService.findAllWithoutOtherObservationArea(id);
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
