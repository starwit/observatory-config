package de.starwit.rest.controller;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import de.starwit.service.impl.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Image RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/imageFile")
public class ImageFileController {

    static final Logger LOG = LoggerFactory.getLogger(ImageFileController.class);

    @Autowired
    private ImageService imageService;

    @Operation(summary = "Get all image")
    @GetMapping
    public List<ImageEntity> findAll() {
        return this.imageService.findAll();
    }

    @Operation(summary = "Get all image without parkingConfig")
    @GetMapping(value = "/find-without-parkingConfig")
    public List<ImageEntity> findAllWithoutParkingConfig() {
        return imageService.findAllWithoutParkingConfig();
    }

    @Operation(summary = "Get all image without other parkingConfig")
    @GetMapping(value = "/find-without-other-parkingConfig/{id}")
    public List<ImageEntity> findAllWithoutOtherParkingConfig(@PathVariable("id") Long id) {
        return imageService.findAllWithoutOtherParkingConfig(id);
    }

    @Operation(summary = "Get image with id")
    @GetMapping(value = "/{id}")
    public ImageEntity findById(@PathVariable("id") Long id) {
        return this.imageService.findById(id);
    }

    @Operation(summary = "Create image")
    @PostMapping
    public ImageEntity save(@Valid @RequestBody ImageEntity entity) {
        return update(entity);
    }

    @Operation(summary = "Update image")
    @PutMapping
    public ImageEntity update(@Valid @RequestBody ImageEntity entity) {
        return imageService.saveOrUpdate(entity);
    }

    @Operation(summary = "Delete image")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        imageService.delete(id);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("Image not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.image.notfound", "Image not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
