package de.starwit.service.streamprocessing;

import java.util.Base64;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.visionapi.Sae.SaeMessage;

/**
 * Decodes an {@code SaeMessage} from the fields of a stream entry written by a vision-lib publisher.
 * The publisher writes either raw protobuf bytes into {@code proto_data} or base64 encoded bytes into
 * {@code proto_data_b64} (depending on its {@code b64_encode} setting), so the field name tells us how to decode.
 * Both formats may appear mixed in the same stream.
 */
public final class SaeMessageDecoder {

    static final String RAW_FIELD = "proto_data";
    static final String B64_FIELD = "proto_data_b64";

    private SaeMessageDecoder() {
    }

    /**
     * @return true if the entry contains one of the known protobuf payload fields
     */
    public static boolean hasPayload(Map<String, byte[]> fields) {
        return fields.get(RAW_FIELD) != null || fields.get(B64_FIELD) != null;
    }

    public static SaeMessage decode(Map<String, byte[]> fields) throws InvalidProtocolBufferException {
        byte[] raw = fields.get(RAW_FIELD);
        if (raw != null) {
            return SaeMessage.parseFrom(raw);
        }

        byte[] b64 = fields.get(B64_FIELD);
        if (b64 != null) {
            byte[] decoded;
            try {
                decoded = Base64.getDecoder().decode(b64);
            } catch (IllegalArgumentException e) {
                throw new InvalidProtocolBufferException("Invalid base64 in field " + B64_FIELD);
            }
            return SaeMessage.parseFrom(decoded);
        }

        throw new InvalidProtocolBufferException(
                "Stream entry contains neither " + RAW_FIELD + " nor " + B64_FIELD + " (fields: " + fields.keySet()
                        + ")");
    }
}
