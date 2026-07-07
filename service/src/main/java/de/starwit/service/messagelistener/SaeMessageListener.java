package de.starwit.service.messagelistener;

import java.util.Base64;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.service.impl.SaeMessageService;
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

    public void handleMessage(MapRecord<String, String, String> message) {
        handle(message, saeMessageService::publishStompMessage);
    }

    public void handleSavingMessage(MapRecord<String, String, String> message) {
        handle(message, saeMessageService::saveMessage);
    }

    private void handle(MapRecord<String, String, String> message, BiConsumer<SaeMessage, String> action) {
        log.debug("Message received: {} from {}", message.getId(), message.getStream());
        String protobuf_data = message.getValue().get("proto_data_b64");
        try {
            SaeMessage saeMessage = SaeMessage.parseFrom(Base64.getDecoder().decode(protobuf_data));
            action.accept(saeMessage, message.getStream());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error decoding proto from message. streamId=" + message.getStream());
            log.debug(e.getMessage());
        }
    }
}
