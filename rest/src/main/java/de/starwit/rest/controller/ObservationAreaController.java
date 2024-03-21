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
import de.starwit.service.dto.ObservationAreaDto;
import de.starwit.service.impl.DatabackendService;
import de.starwit.service.impl.ObservationAreaService;
import de.starwit.service.mapper.ObservationAreaMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/**
 * ObservationArea RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/observationarea")
public class ObservationAreaController {

    static final Logger LOG = LoggerFactory.getLogger(ObservationAreaController.class);

    @Autowired
    private ObservationAreaService observationareaService;

    @Autowired
    private DatabackendService databackendService;

    private ObservationAreaMapper mapper = new ObservationAreaMapper();

    @Operation(summary = "Get all observationarea")
    @GetMapping
    public List<ObservationAreaDto> findAll() {
        return mapper.convertToDtoList(this.observationareaService.findAll());
    }

    @Operation(summary = "Get observationarea with id")
    @GetMapping(value = "/{id}")
    public ObservationAreaDto findById(@PathVariable("id") Long id) {
        return mapper.convertToDto(this.observationareaService.findById(id));
    }

    @Operation(summary = "Create observationarea")
    @PostMapping
    public ObservationAreaDto save(@Valid @RequestBody ObservationAreaDto dto) {
        return update(dto);
    }

    @Operation(summary = "Update observationarea")
    @PutMapping
    public ObservationAreaDto update(@Valid @RequestBody ObservationAreaDto dto) {
        return observationareaService.saveOrUpdateDto(dto);
    }

    @Operation(summary = "Delete observationarea")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        observationareaService.delete(id);
        databackendService.triggerConfigurationSync();
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("ObservationArea not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.observationarea.notfound", "ObservationArea not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
