package de.starwit.rest.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import de.starwit.service.dto.FileDto;
import de.starwit.service.dto.ImageDto;
import de.starwit.service.impl.ImageService;
import de.starwit.service.impl.ObservationAreaService;
import de.starwit.service.mapper.ImageMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/**
 * Image RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/image")
public class ImageController {

    static final Logger LOG = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private ObservationAreaService observationAreaService;

    @Autowired
    private ImageMapper mapper;

    @Operation(summary = "Get all image")
    @GetMapping
    public List<ImageEntity> findAll() {
        return this.imageService.findAll();
    }

    @Operation(summary = "Get all image without other observationAreas")
    @GetMapping(value = "/find-with-polygons/{id}")
    public List<ImageDto> findFindWithPolygons(@PathVariable("id") Long id) {
        List<ImageEntity> images = imageService.findByObservationAreaId(id);
        return mapper.convertToDtoList(images);
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
        return imageService.saveMetadata(entity);
    }

    @PostMapping("/upload/{observationareaid}")
    @CrossOrigin
    public void uploadImage(@RequestParam("image") MultipartFile file, @PathVariable("observationareaid") Long id)
            throws IOException {
        ObservationAreaEntity observationAreaEntity = observationAreaService.findById(id);
        imageService.uploadImage(file, observationAreaEntity);
    }

    @GetMapping(value = "/as-file/{id}")
    public ResponseEntity<Resource> getImageWithIdAsFile(@PathVariable("id") Long id) {
        FileDto file = imageService.getImageAsFile(id);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        header.add("Cache-Control", "max-age=3600, must-revalidate");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.getFileSize())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file.getByteArrayResource());
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
