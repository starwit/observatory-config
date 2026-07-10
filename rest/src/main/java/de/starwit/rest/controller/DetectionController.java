package de.starwit.rest.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.starwit.persistence.entity.DetectionEntity;
import de.starwit.rest.dtos.TrajectoriesByClassDto;
import de.starwit.rest.dtos.TimeWindowRequestDto;
import de.starwit.rest.dtos.TracedObjectDto;
import de.starwit.rest.exception.NotificationDto;
import de.starwit.service.impl.DetectionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping(path = "${rest.base-path}/detection")
public class DetectionController {

    static final Logger LOG = LoggerFactory.getLogger(DetectionController.class);

    @Autowired
    private DetectionService detectionService;

    @Operation(summary = "Get detection with id")
    @GetMapping(value = "/{id}")
    public DetectionEntity findById(@PathVariable("id") Long id) {
        return detectionService.findById(id);
    }

    @Operation(summary = "Get trajectories grouped by class for a time window")
    @PostMapping(value = "/trajectories")
    public List<TrajectoriesByClassDto> findTrajectoriesInWindow(@RequestBody TimeWindowRequestDto requestData) {
        List<DetectionEntity> detections = detectionService.findDetectionsInWindow(
                requestData.getTimestamp(), Duration.ofMinutes(requestData.getWindowSize()), requestData.getStreamId());

        Map<Integer, Map<String, List<DetectionEntity>>> byClassAndObject = detections.stream()
                .collect(Collectors.groupingBy(DetectionEntity::getClassId,
                        Collectors.groupingBy(DetectionEntity::getObjectId)));

        List<TrajectoriesByClassDto> result = new ArrayList<>();
        result = byClassAndObject.entrySet().stream()
                    .map(this::convertToTrajectoryDTO)
                    .collect(Collectors.toList());
        
        return result;
    }

    private TrajectoriesByClassDto convertToTrajectoryDTO(Map.Entry<Integer, Map<String, List<DetectionEntity>>> classEntry) {

        List<TracedObjectDto> tracedObjects = classEntry.getValue().entrySet().stream()
                .map(objectEntry -> {
                    // sort detections by timestamp, to get first/last entry
                    List<DetectionEntity> points = objectEntry.getValue().stream()
                            .sorted((a, b) -> a.getDetectionTimestamp().compareTo(b.getDetectionTimestamp()))
                            .toList();

                    TracedObjectDto dto = new TracedObjectDto();
                    dto.setObjectId(objectEntry.getKey());
                    dto.setStart(points.get(0).getDetectionTimestamp().toOffsetDateTime());
                    dto.setEnd(points.get(points.size() - 1).getDetectionTimestamp().toOffsetDateTime());
                    // convert detection coordinates to points
                    dto.setTrajectory(points.stream()
                            .map(point -> new Point(point.getX() != null ? point.getX() : 0.0, point.getY() != null ? point.getY() : 0.0))
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
        return new TrajectoriesByClassDto(classEntry.getKey(), tracedObjects);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("Detection not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.detection.notfound", "Detection not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
