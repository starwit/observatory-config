package de.starwit.service.streamprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.repository.CameraRepository;

@Component
@ConditionalOnProperty(name = "spring.data.redis.active", havingValue = "true", matchIfMissing = true)
public class StreamRecordingService {

    private Logger log = LoggerFactory.getLogger(StreamRecordingService.class);

    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer;

    @Autowired
    private StreamAvailabilityService streamAvailabilityService;

    @Autowired
    private SaeMessageListener messageListener;

    @Autowired
    private CameraRepository cameraRepository;

    private Map<String, Subscription> streamToSubscription = new HashMap<>();

    @Scheduled(fixedRate = 2000)
    public synchronized void synchronizeSubscriptions() {
        List<String> streamsToRecord = getStreamsToRecord();

        log.debug("available streams for recording" + streamAvailabilityService.getAvailableStreams().toString());
        log.debug("Streams marked for recording " + streamsToRecord.toString());

        for (String streamId : streamAvailabilityService.getAvailableStreams()) {
            if (streamsToRecord.contains(streamId) && !streamToSubscription.containsKey(streamId)) {
                log.info("Added saving subscription for " + streamId);
                Subscription subscription = streamMessageListenerContainer.receive(
                        StreamOffset.latest(streamId), messageListener::handleSavingMessage);
                streamToSubscription.put(streamId, subscription);
            }
        }

        List<String> toRemove = streamToSubscription.keySet().stream()
                .filter(streamId -> !streamsToRecord.contains(streamId))
                .toList();
        for (String streamId : toRemove) {
            streamMessageListenerContainer.remove(streamToSubscription.remove(streamId));
            log.info("Removed saving subscription for " + streamId);
        }
    }

    public List<String> getAvailableStreams() {
        return streamAvailabilityService.getAvailableStreams();
    }

    public List<String> getStreamsToRecord() {
        return cameraRepository.findByRecordingEnabledTrue().stream()
                .map(CameraEntity::getSaeStreamKey)
                .toList();
    }

    public void stopAllRecordings() {
        int clearedCount = cameraRepository.clearAllRecordingFlags();
        log.debug("Stopped recording for " + clearedCount + " camera(s).");
    }

    public void setRecordingEnabled(String streamName, boolean recordingEnabled) {
        CameraEntity camera = cameraRepository.findBySaeStreamKey(streamName);
        if (camera == null) {
            log.warn("No camera found for stream " + streamName + ", cannot change its recording status.");
            return;
        }
        if (camera.isRecordingEnabled() == recordingEnabled) {
            log.debug("Stream " + streamName + " already has recording status " + recordingEnabled + ".");
            return;
        }
        camera.setRecordingEnabled(recordingEnabled);
        cameraRepository.saveAndFlush(camera);
        log.debug("Set recording status of stream " + streamName + " to " + recordingEnabled + ".");
    }
}
