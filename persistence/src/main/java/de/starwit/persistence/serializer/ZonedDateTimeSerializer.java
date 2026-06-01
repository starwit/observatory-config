package de.starwit.persistence.serializer;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.jackson.JacksonComponent;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

@JacksonComponent
public class ZonedDateTimeSerializer {

    public static class Serializer extends ValueSerializer<ZonedDateTime> {

        @Override
        public void serialize(ZonedDateTime date, JsonGenerator jgen, SerializationContext context) {
            jgen.writeString(date != null ? ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")) : null);
            ;
        }

    }

    public static class Deserializer extends ValueDeserializer<ZonedDateTime> {

        @Override
        public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
            return ZonedDateTime.parse(jsonParser.getString());
        }
    }
}