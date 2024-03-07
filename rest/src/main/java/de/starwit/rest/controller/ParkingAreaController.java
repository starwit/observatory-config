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

import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import de.starwit.service.dto.ParkingAreaDto;
import de.starwit.service.impl.ParkingAreaService;
import de.starwit.service.mapper.ParkingAreaMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

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


    private ParkingAreaMapper mapper = new ParkingAreaMapper();

    @Operation(summary = "Get all parkingarea")
    @GetMapping
    public List<ParkingAreaDto> findAll() {
        return mapper.convertToDtoList(this.parkingareaService.findAll());
    }

    @Operation(summary = "Get parkingarea with id")
    @GetMapping(value = "/{id}")
    public ParkingAreaDto findById(@PathVariable("id") Long id) {
        return mapper.convertToDto(this.parkingareaService.findById(id));
    }

    @Operation(summary = "Create parkingarea")
    @PostMapping
    public ParkingAreaDto save(@Valid @RequestBody ParkingAreaDto dto) {
        return update(dto);
    }

    @Operation(summary = "Update parkingarea")
    @PutMapping
    public ParkingAreaDto update(@Valid @RequestBody ParkingAreaDto dto) {
        return parkingareaService.saveOrUpdateDto(dto);
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
