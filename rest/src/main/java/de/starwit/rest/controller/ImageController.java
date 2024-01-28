package de.starwit.rest.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import de.starwit.service.dto.ImageDto;
import de.starwit.service.dto.RegionDto;
import de.starwit.service.impl.ClassificationService;
import de.starwit.service.impl.DatabackendService;
import de.starwit.service.impl.ImageService;
import de.starwit.service.impl.ParkingConfigService;
import de.starwit.service.impl.PointService;
import de.starwit.service.impl.PolygonService;
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
    private ClassificationService classificationService;

    @Autowired
    private PolygonService polygonService;

    @Autowired
    private PointService pointService;

    @Autowired
    private ParkingConfigService parkingConfigService;

    @Autowired
    private DatabackendService databackendService;

    @Autowired
    private ImageMapper mapper;

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

    @Operation(summary = "Get all image without other parkingConfig")
    @GetMapping(value = "/find-with-polygons/{id}")
    public List<ImageDto> findFindWithPolygons(@PathVariable("id") Long id) {
        List<ImageEntity> images = imageService.findByParkingConfigId(id);
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

    @Operation(summary = "Save polygon to image")
    @PostMapping(value = "/save-polygons")
    public void savePolygons(@Valid @RequestBody List<ImageDto> dtos) {
        for (ImageDto dto : dtos) {
            savePolygonsPerImage(dto);
        }
    }

    @PostMapping("/upload/{parkingconfigid}")
    @CrossOrigin
    public void uploadImage(@RequestParam("image") MultipartFile file, @PathVariable("parkingconfigid") Long id)
            throws IOException {
        ParkingConfigEntity prodConfigEntity = parkingConfigService.findById(id);
        imageService.uploadImage(file, prodConfigEntity);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> getImageByName(@PathVariable("id") Long id) {
        byte[] image = imageService.findById(id).getData();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    private ImageDto savePolygonsPerImage(ImageDto dto) throws EntityNotFoundException {
        ImageEntity entity = new ImageEntity();
        if (dto.getId() != null) {
            entity = imageService.findById(dto.getId());
            if (entity.getPolygon() != null) {
                polygonService.deleteAll(entity.getPolygon());
                polygonService.getRepository().flush();
            }
            entity.getPolygon().removeAll(entity.getPolygon());
            entity = imageService.saveAndFlush(entity);
            List<RegionDto> regions = dto.getRegions();
            for (RegionDto regionDto : regions) {
                if (regionDto.getType().equals("polygon")) {
                    entity.addToPolygons(createPolygon(entity, regionDto));
                } else if (regionDto.getType().equals("line")) {
                    entity.addToPolygons(createLine(entity, regionDto));
                }
            }

            entity = imageService.saveOrUpdate(entity);
            databackendService.syncConfiguration(dto);

            return mapper.convertToDto(entity);

        } else {
            throw new EntityNotFoundException();
        }
    }

    private PolygonEntity createPolygon(ImageEntity entity, RegionDto regionDto) {
        PolygonEntity polygonEntity = new PolygonEntity();
        Set<ClassificationEntity> cls = classificationService.findByName(regionDto.getCls());
        polygonEntity.setClassification(cls);
        polygonEntity.setOpen(regionDto.getOpen());
        polygonEntity.setImage(entity);
        polygonEntity.setName(regionDto.getName());
        polygonEntity = polygonService.saveAndFlush(polygonEntity);
        List<List<Double>> points = regionDto.getPoints();
        if (points != null && !points.isEmpty()) {
            for (List<Double> point : regionDto.getPoints()) {
                PointEntity pointEntity = new PointEntity();
                pointEntity.setXvalue(BigDecimal.valueOf(Math.max(0, Math.min(1, point.get(0)))));
                pointEntity.setYvalue(BigDecimal.valueOf(Math.max(0, Math.min(1, point.get(1)))));
                pointEntity.setPolygon(polygonEntity);
                pointEntity = pointService.saveAndFlush(pointEntity);
                polygonEntity.addToPoints(pointEntity);
            }
        }
        return polygonEntity;
    }

    private PolygonEntity createLine(ImageEntity entity, RegionDto regionDto) {
        PolygonEntity polygonEntity = new PolygonEntity();
        Set<ClassificationEntity> cls = classificationService.findByName(regionDto.getCls());
        polygonEntity.setClassification(cls);
        polygonEntity.setOpen(true);
        polygonEntity.setImage(entity);
        polygonEntity.setName(regionDto.getName());
        polygonEntity = polygonService.saveAndFlush(polygonEntity);
        PointEntity p1 = new PointEntity();
        p1.setXvalue(BigDecimal.valueOf(Math.max(0, Math.min(1, regionDto.getX1()))));
        p1.setYvalue(BigDecimal.valueOf(Math.max(0, Math.min(1, regionDto.getY1()))));
        p1.setPolygon(polygonEntity);
        p1 = pointService.saveAndFlush(p1);
        polygonEntity.addToPoints(p1);

        PointEntity p2 = new PointEntity();
        p2.setXvalue(BigDecimal.valueOf(Math.max(0, Math.min(1, regionDto.getX2()))));
        p2.setYvalue(BigDecimal.valueOf(Math.max(0, Math.min(1, regionDto.getY2()))));
        p2.setPolygon(polygonEntity);
        p2 = pointService.saveAndFlush(p2);
        polygonEntity.addToPoints(p2);
        return polygonEntity;
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
