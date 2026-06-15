package de.starwit.service.sae;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.visionapi.Sae.BoundingBox;
import de.starwit.visionapi.Sae.Detection;
import de.starwit.visionapi.Sae.SaeMessage;
import de.starwit.visionapi.Sae.Shape;

import java.util.ArrayList;

@Service
public class MessageService {

    private Logger log = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private SimpMessagingTemplate template; 
    
    private List<String> availableStreams = new ArrayList<>();

    public void handleMessage(MapRecord<String, String, String> message) {
        log.debug("Message received: {} from {}", message.getId(), message.getStream());
        String protobuf_data = message.getValue().get("proto_data_b64");
        try {
            SaeMessage msg = SaeMessage.parseFrom(Base64.getDecoder().decode(protobuf_data));
            List<TrajectoryDto> trackedObjects = convertToDTOs(msg, message.getStream());
            this.template.convertAndSend("/topic/location/" + message.getStream(), trackedObjects);
            log.debug("Sent " + trackedObjects.size() + " messages");
        } catch (InvalidProtocolBufferException e) {
            log.error("Error decoding proto from message. streamId=" + message.getStream());
            log.debug(e.getMessage());
        }
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

    public List<String> getAvailableStreams() {
        return availableStreams;
    }

    public void setAvailableStreams(List<String> availableStreams) {
        this.availableStreams = availableStreams;
    }
}
