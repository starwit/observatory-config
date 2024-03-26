package de.starwit.rest.controller;

import java.math.BigDecimal;
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

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import de.starwit.service.dto.ObservationAreaDto;
import de.starwit.service.dto.RegionDto;
import de.starwit.service.impl.ClassificationService;
import de.starwit.service.impl.DatabackendService;
import de.starwit.service.impl.ObservationAreaService;
import de.starwit.service.impl.PointService;
import de.starwit.service.impl.PolygonService;
import de.starwit.service.mapper.ObservationAreaMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "${rest.base-path}/observationarea")
public class ObservationAreaController {

    static final Logger LOG = LoggerFactory.getLogger(ObservationAreaController.class);

    @Autowired
    private ObservationAreaService observationareaService;

    @Autowired
    private DatabackendService databackendService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private PolygonService polygonService;

    @Autowired
    private PointService pointService;

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

    @Operation(summary = "Save polygon to ObservationArea")
    @PostMapping(value = "/save-polygons/{id}")
    public void savePolygons(@PathVariable Long id, @Valid @RequestBody List<RegionDto> polygons) {
        ObservationAreaEntity oae = observationareaService.findById(id);
        if(oae.getPolygon() != null) {
            polygonService.deleteAll(oae.getPolygon());
            polygonService.getRepository().flush();
        }

        oae.getPolygon().removeAll(oae.getPolygon());
        oae = observationareaService.saveAndFlush(oae);
        
        for (RegionDto regionDto : polygons) {
            if ("polygon".equals(regionDto.getType())) {
                createPolygon(oae, regionDto);
            }
            if ("line".equals(regionDto.getType())) {
                createLine(oae, regionDto);
            }
        }
        databackendService.triggerConfigurationSync();
    }       
 
    private PolygonEntity createPolygon(ObservationAreaEntity entity, RegionDto regionDto) {
        PolygonEntity polygonEntity = new PolygonEntity();
        ClassificationEntity cls = classificationService.findByName(regionDto.getCls());
        polygonEntity.setClassification(cls);
        polygonEntity.setOpen(regionDto.getOpen());
        polygonEntity.setObservationArea(entity);
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

    private PolygonEntity createLine(ObservationAreaEntity entity, RegionDto regionDto) {
        PolygonEntity polygonEntity = new PolygonEntity();
        ClassificationEntity cls = classificationService.findByName(regionDto.getCls());
        polygonEntity.setClassification(cls);
        polygonEntity.setOpen(true);
        polygonEntity.setObservationArea(entity);
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

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("ObservationArea not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.observationarea.notfound", "ObservationArea not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
