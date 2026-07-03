package de.starwit.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
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

import de.starwit.service.messagelistener.SaeMessageListener;

@Component
@ConditionalOnProperty(name = "spring.data.redis.active", havingValue = "true", matchIfMissing = true)
public class StreamSavingService {

    private Logger log = LoggerFactory.getLogger(StreamSavingService.class);

    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer;

    @Autowired
    private StreamAvailabilityService streamAvailabilityService;

    @Autowired
    private SaeMessageListener messageService;

    private List<String> streamsToRecord;

    private Map<String, Subscription> streamToSubscription = new HashMap<>();

    @Scheduled(fixedRate = 2000)
    public synchronized void synchronizeSubscriptions() {

        log.debug("available streams for recording");
        log.debug(streamAvailabilityService.getAvailableStreams().toString());
        
        if(streamsToRecord == null) {
            log.debug("streamsToRecord is null, initializing it.");
            streamsToRecord = new LinkedList<>();
        }

        for (String streamId : streamAvailabilityService.getAvailableStreams()) {
            if (streamsToRecord.contains(streamId) && !streamToSubscription.containsKey(streamId)) {
                log.info("Added saving subscription for " + streamId);
                Subscription subscription = streamMessageListenerContainer.receive(
                        StreamOffset.latest(streamId), messageService::handleSavingMessage);
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

    public void addStreamToRecord(String streamName) {
        if(streamsToRecord.contains(streamName)) {
            log.debug("Stream " + streamName + " is already in the list of streams to record.");
            return;
        } else {
            streamsToRecord.add(streamName);
            log.debug("Stream " + streamName + " added to the list of streams to record.");

        }
    }

    public void removeStreamFromRecord(String streamName) {
        log.debug("remove stream from recording " + streamName);
        streamsToRecord.remove(streamName);
    }

    public List<String> getStreamsToRecord() {
        return streamsToRecord;
    }
}
