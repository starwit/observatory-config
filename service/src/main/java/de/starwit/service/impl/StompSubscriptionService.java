package de.starwit.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import de.starwit.service.messagelistener.SaeMessageListener;

@Component
@ConditionalOnProperty(name = "spring.data.redis.active", havingValue = "true", matchIfMissing = true)
public class StompSubscriptionService implements ApplicationListener<AbstractSubProtocolEvent> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer;

    @Autowired
    private SaeMessageListener messageService;

    private Map<String, List<String>> sessionIdToDestinations = new HashMap<>();

    private Map<String, Subscription> destinationToSubscription = new HashMap<>();

    @Override
    public synchronized void onApplicationEvent(AbstractSubProtocolEvent event) {
        log.debug("Received event of type " + event.getClass().getSimpleName());

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (event instanceof SessionSubscribeEvent) {
            String destination = headerAccessor.getDestination();
            log.debug("SUBSCRIBE to " + destination + "(sessionId=" + sessionId + ")");

            this.addDestinationToSession(sessionId, destination);

            log.debug("Added " + sessionId + " for " + destination);
        } else if (event instanceof SessionUnsubscribeEvent || event instanceof SessionDisconnectEvent) {
            log.debug("Session ended " + sessionId);

            this.removeSession(sessionId);
        }

        synchronizeSubscriptions();

        if (log.isDebugEnabled()) {
            log.debug("Active destinations: " + Arrays.toString(getUniqueDestinations().toArray()));
        }

    }

    private void addDestinationToSession(String sessionId, String destination) {
        List<String> destinations = sessionIdToDestinations.get(sessionId);
        if (destinations == null) {
            destinations = new ArrayList<>();
            sessionIdToDestinations.put(sessionId, destinations);
        }
        destinations.add(destination);
    }

    private void removeSession(String sessionId) {
        sessionIdToDestinations.remove(sessionId);
    }

    private void synchronizeSubscriptions() {
        List<String> activeDestinations = getUniqueDestinations();
        List<String> subscribedDestinations = new ArrayList<>(destinationToSubscription.keySet());

        // Make sure that there is an active subscription for all active destinations
        // (i.e. with sessions attached)
        for (String destination : activeDestinations) {
            if (!destinationToSubscription.containsKey(destination)) {
                String streamId = streamIdFromDestination(destination);
                log.info("Added subscription for " + streamId);
                Subscription subscription = streamMessageListenerContainer.receive(StreamOffset.latest(streamId),
                        messageService::handleMessage);
                destinationToSubscription.put(destination, subscription);
            }
        }

        // Remove subscriptions that are not longer needed
        for (String destination : subscribedDestinations) {
            if (!activeDestinations.contains(destination)) {
                Subscription subToRemove = destinationToSubscription.remove(destination);
                if (subToRemove != null) {
                    streamMessageListenerContainer.remove(subToRemove);
                }
                log.info("Removed subscription for " + streamIdFromDestination(destination));
            }
        }

    }

    private String streamIdFromDestination(String destination) {
        return destination.substring(destination.lastIndexOf("/") + 1);
    }

    private List<String> getUniqueDestinations() {
        return sessionIdToDestinations.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
    }
}