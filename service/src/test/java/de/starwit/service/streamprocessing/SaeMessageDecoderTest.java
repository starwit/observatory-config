package de.starwit.service.streamprocessing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.visionapi.Sae.SaeMessage;
import de.starwit.visionapi.Sae.VideoFrame;

public class SaeMessageDecoderTest {

    private static final SaeMessage MSG = SaeMessage.newBuilder()
            .setFrame(VideoFrame.newBuilder().setSourceId("cam1").setTimestampUtcMs(1234567890123L))
            .build();

    @Test
    public void testDecodesRaw() throws Exception {
        SaeMessage decoded = SaeMessageDecoder.decode(Map.of("proto_data", MSG.toByteArray()));

        assertThat(decoded).isEqualTo(MSG);
    }

    @Test
    public void testDecodesBase64() throws Exception {
        byte[] b64 = Base64.getEncoder().encode(MSG.toByteArray());

        SaeMessage decoded = SaeMessageDecoder.decode(Map.of("proto_data_b64", b64));

        assertThat(decoded).isEqualTo(MSG);
    }

    @Test
    public void testRejectsMissingField() {
        Map<String, byte[]> fields = Map.of("other", new byte[] { 1 });

        assertThat(SaeMessageDecoder.hasPayload(fields)).isFalse();
        assertThatThrownBy(() -> SaeMessageDecoder.decode(fields))
                .isInstanceOf(InvalidProtocolBufferException.class);
    }

    @Test
    public void testRejectsInvalidBase64() {
        assertThatThrownBy(() -> SaeMessageDecoder.decode(Map.of("proto_data_b64", "not base64!".getBytes())))
                .isInstanceOf(InvalidProtocolBufferException.class);
    }
}
