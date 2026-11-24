package de.starwit.service.streamprocessing;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.visionapi.Sae.SaeMessage;

/**
 * Receives SAE messages from the Redis stream listener container, deserializes the protobuf payload and hands the
 * resulting {@link SaeMessage} off to the {@link SaeMessageService} for further processing.
 */
@Service
public class SaeMessageListener {

    private Logger log = LoggerFactory.getLogger(SaeMessageListener.class);

    @Autowired
    private SaeMessageService saeMessageService;

    public void handleMessage(MapRecord<String, String, byte[]> message) {
        handle(message, saeMessageService::publishStompMessage);
    }

    public void handleSavingMessage(MapRecord<String, String, byte[]> message) {
        handle(message, saeMessageService::saveMessage);
    }

    /*
     * Must never throw: an exception escaping a stream listener makes Spring cancel the subscription, after which
     * every following message on that stream is silently dropped.
     */
    private void handle(MapRecord<String, String, byte[]> message, BiConsumer<SaeMessage, String> action) {
        log.debug("Message received: {} from {}", message.getId(), message.getStream());
        try {
            SaeMessage saeMessage = SaeMessageDecoder.decode(message.getValue());
            action.accept(saeMessage, message.getStream());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error decoding proto from message. streamId={}, entryId={}", message.getStream(),
                    message.getId());
            log.debug(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error handling message. streamId={}, entryId={}", message.getStream(), message.getId(), e);
        }
    }
}
