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

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.service.impl.ClassificationService;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Classification RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/classification")
public class ClassificationController {

    static final Logger LOG = LoggerFactory.getLogger(ClassificationController.class);

    @Autowired
    private ClassificationService classificationService;

    @Operation(summary = "Get all classification")
    @GetMapping
    public List<ClassificationEntity> findAll() {
        return this.classificationService.findAll();
    }


    @Operation(summary = "Get classification with id")
    @GetMapping(value = "/{id}")
    public ClassificationEntity findById(@PathVariable("id") Long id) {
        return this.classificationService.findById(id);
    }

    @Operation(summary = "Create classification")
    @PostMapping
    public ClassificationEntity save(@Valid @RequestBody ClassificationEntity entity) {
        return update(entity);
    }

    @Operation(summary = "Update classification")
    @PutMapping
    public ClassificationEntity update(@Valid @RequestBody ClassificationEntity entity) {
        return classificationService.saveOrUpdate(entity);
    }

    @Operation(summary = "Delete classification")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        classificationService.delete(id);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("Classification not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.classification.notfound", "Classification not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
