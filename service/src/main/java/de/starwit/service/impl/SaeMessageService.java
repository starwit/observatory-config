package de.starwit.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import de.starwit.service.dto.TrajectoryDto;
import de.starwit.visionapi.Sae.BoundingBox;
import de.starwit.visionapi.Sae.Detection;
import de.starwit.visionapi.Sae.SaeMessage;
import de.starwit.visionapi.Sae.Shape;

/**
 * Serves SAE visualizer message data to the frontend: exposes the currently available streams to the REST controller
 * and converts incoming SAE messages into {@link TrajectoryDto}s that are pushed to the websocket clients.
 */
@Service
public class SaeMessageService {

    private Logger log = LoggerFactory.getLogger(SaeMessageService.class);

    @Autowired
    private SimpMessagingTemplate template;

    // Optional: the stream monitor is only present when the Redis stream listener is enabled (spring.redis.enabled).
    @Autowired(required = false)
    private StreamAvailabilityService streamMonitorService;

    public List<String> getAvailableStreams() {
        if (streamMonitorService == null) {
            return List.of();
        }
        return streamMonitorService.getAvailableStreams();
    }

    public void publishStompMessage(SaeMessage saeMessage, String streamId) {
        List<TrajectoryDto> trackedObjects = convertToDTOs(saeMessage, streamId);
        this.template.convertAndSend("/topic/location/" + streamId, trackedObjects);
        log.debug("Sent " + trackedObjects.size() + " messages");
    }

    private List<TrajectoryDto> convertToDTOs(SaeMessage saeMessage, String streamId) {

        List<TrajectoryDto> trackedObjects = new ArrayList<>();

        Shape shape = saeMessage.getFrame().getShape();

        for (Detection detection : saeMessage.getDetectionsList()) {
            var t = new TrajectoryDto();
            byte[] objectID = detection.getObjectId().toByteArray();
            t.setObjectId(HexFormat.of().formatHex(objectID));
            t.setClassId(detection.getClassId());
            t.setReceiveTimestamp(LocalDateTime.now());
            t.setStreamId(streamId);
            t.setShape(shape.getWidth(), shape.getHeight());
            t.setHasGeoCoordinates(false);
            t = setNormalizedImageCoordinates(t, detection.getBoundingBox());
            t = setBoundingBox(t, detection.getBoundingBox(), shape);
            if (detection.hasGeoCoordinate()) {
                t.setHasGeoCoordinates(true);
                t.getCoordinates().setLatitude(detection.getGeoCoordinate().getLatitude());
                t.getCoordinates().setLongitude(detection.getGeoCoordinate().getLongitude());
            }
            trackedObjects.add(t);
        }

        return trackedObjects;
    }

    private TrajectoryDto setNormalizedImageCoordinates(TrajectoryDto t, BoundingBox bb) {
        // compute center of bounding box
        float x = ((bb.getMinX() + bb.getMaxX()) / 2) * t.getShape().getWidth();
        float y = ((bb.getMinY() + bb.getMaxY()) / 2) * t.getShape().getHeight();
        //log.info("bb: " + bb.getMaxX() + ", " + bb.getMinX() + "; " + x + "," + y);
        t.getCoordinates().setX(x);
        t.getCoordinates().setY(y);
        return t;
    }

    private TrajectoryDto setBoundingBox(TrajectoryDto t, BoundingBox bb, Shape sh) {
        t.getBoundingBox().setMinX(bb.getMinX() * sh.getWidth());
        t.getBoundingBox().setMinY(bb.getMinY() * sh.getHeight());
        t.getBoundingBox().setMaxX(bb.getMaxX() * sh.getWidth());
        t.getBoundingBox().setMaxY(bb.getMaxY() * sh.getHeight());
        return t;
    }
}
