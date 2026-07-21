package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Tests for RecordingPartitionService
 */
public class RecordingPartitionServiceTest {

    private RecordingPartitionService serviceWithInitTime(LocalTime partitionInitTimeUtc) {
        RecordingPartitionService service = new RecordingPartitionService(null);
        ReflectionTestUtils.setField(service, "PARTITION_INIT_TIME_UTC", partitionInitTimeUtc);
        return service;
    }

    private static Instant now(String isoDateTimeUtc) {
        return expected(isoDateTimeUtc).toInstant();
    }

    private static OffsetDateTime expected(String isoDateTimeUtc) {
        return OffsetDateTime.parse(isoDateTimeUtc + "Z");
    }

    @Test
    public void testNowBeforeInitTimeReturnsPreviousDay() {
        RecordingPartitionService service = serviceWithInitTime(LocalTime.parse("03:00"));

        assertEquals(expected("2026-07-20T03:00:00"), service.calcPartitionInit(now("2026-07-21T02:59:59")));
    }

    @Test
    public void testNowAfterInitTimeReturnsSameDay() {
        RecordingPartitionService service = serviceWithInitTime(LocalTime.parse("03:00"));

        assertEquals(expected("2026-07-21T03:00:00"), service.calcPartitionInit(now("2026-07-21T03:00:01")));
    }

    @Test
    public void testNowExactlyAtInitTimeReturnsSameDay() {
        RecordingPartitionService service = serviceWithInitTime(LocalTime.parse("03:00"));

        assertEquals(expected("2026-07-21T03:00:00"), service.calcPartitionInit(now("2026-07-21T03:00:00")));
    }

    @Test
    public void testMidnightInitTimeAlwaysReturnsSameDay() {
        RecordingPartitionService service = serviceWithInitTime(LocalTime.MIDNIGHT);

        assertEquals(expected("2026-07-21T00:00:00"), service.calcPartitionInit(now("2026-07-21T00:00:00")));
        assertEquals(expected("2026-07-21T00:00:00"), service.calcPartitionInit(now("2026-07-21T23:59:59")));
    }

    @Test
    public void testRollsBackOverLeapDay() {
        RecordingPartitionService service = serviceWithInitTime(LocalTime.parse("03:00"));

        assertEquals(expected("2024-02-29T03:00:00"), service.calcPartitionInit(now("2024-03-01T01:00:00")));
    }
}
