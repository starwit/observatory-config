package de.starwit.persistence.partitioning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Tests for RecordingPartitionService
 */
public class RecordingPartitionServiceTest {

    private static final String PARENT_TABLE = "detection";

    // Partition parameters the mocked service is configured with
    private static final LocalTime PARTITION_INIT_TIME_UTC = LocalTime.MIDNIGHT;
    private static final Duration PARTITION_RANGE = Duration.ofDays(1);
    private static final Duration RETENTION_TIME = Duration.ofDays(1);

    private RecordingPartitionService serviceWithInitTime(LocalTime partitionInitTimeUtc) {
        RecordingPartitionService service = new RecordingPartitionService();
        ReflectionTestUtils.setField(service, "PARTITION_INIT_TIME_UTC", partitionInitTimeUtc);
        return service;
    }

    /**
     * Service wired to a mocked provisioner, configured with the partition parameters above.
     */
    private RecordingPartitionService serviceWithProvisioner(PartitionDdlProvisioner ddlProvisioner) {
        RecordingPartitionService service = serviceWithInitTime(PARTITION_INIT_TIME_UTC);
        ReflectionTestUtils.setField(service, "ddlProvisioner", ddlProvisioner);
        ReflectionTestUtils.setField(service, "PARTITION_RANGE", PARTITION_RANGE);
        ReflectionTestUtils.setField(service, "RETENTION_TIME", RETENTION_TIME);
        return service;
    }

    private static PartitionInfoDto partition(String name, OffsetDateTime from, OffsetDateTime to) {
        return new PartitionInfoDto(name, from, to, from.toString(), to.toString());
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

    // --- Smoke tests for the scheduled maintenance ------------------------

    @Test
    public void testInitOnUnsupportedDatabaseSkipsAllPartitionWork() {
        PartitionDdlProvisioner ddlProvisioner = mock(PartitionDdlProvisioner.class);
        when(ddlProvisioner.isPartitioningSupported()).thenReturn(false);
        RecordingPartitionService service = serviceWithProvisioner(ddlProvisioner);

        service.init();
        service.runPartitionMaintenance();

        verify(ddlProvisioner).isPartitioningSupported();
        verifyNoMoreInteractions(ddlProvisioner);
    }

    @Test
    public void testInitCreatesContiguousPartitionsCoveringTheLookahead() {
        PartitionDdlProvisioner ddlProvisioner = mock(PartitionDdlProvisioner.class);
        when(ddlProvisioner.isPartitioningSupported()).thenReturn(true);
        when(ddlProvisioner.fetchPartitions(PARENT_TABLE)).thenReturn(List.of());
        RecordingPartitionService service = serviceWithProvisioner(ddlProvisioner);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime todaysInitTime = now.toLocalDate().atTime(PARTITION_INIT_TIME_UTC).atOffset(ZoneOffset.UTC);

        service.init();

        ArgumentCaptor<OffsetDateTime> from = ArgumentCaptor.forClass(OffsetDateTime.class);
        ArgumentCaptor<OffsetDateTime> to = ArgumentCaptor.forClass(OffsetDateTime.class);
        verify(ddlProvisioner, atLeastOnce()).createPartitionIfMissing(eq(PARENT_TABLE), from.capture(), to.capture());

        List<OffsetDateTime> fromBounds = from.getAllValues();
        List<OffsetDateTime> toBounds = to.getAllValues();

        // Without existing partitions the first one starts at today's init time
        assertEquals(todaysInitTime, fromBounds.get(0));
        for (int i = 0; i < fromBounds.size(); i++) {
            assertEquals(fromBounds.get(i).plus(PARTITION_RANGE), toBounds.get(i),
                    "partition " + i + " must span " + PARTITION_RANGE);
            if (i > 0) {
                assertEquals(toBounds.get(i - 1), fromBounds.get(i), "partitions must not leave gaps");
            }
        }
        // Partitions have to reach one whole range into the future
        assertFalse(toBounds.get(toBounds.size() - 1).isBefore(now.plus(PARTITION_RANGE)),
                "last partition must cover the lookahead window");
    }

    @Test
    public void testMaintenanceDropsOnlyExpiredPartitionsAndContinuesAfterTheLastOne() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime cutoff = now.minus(RETENTION_TIME);
        // Ends before the cutoff, so it is expired
        PartitionInfoDto expired = partition("detection_p_expired",
                cutoff.minus(PARTITION_RANGE.multipliedBy(2)), cutoff.minus(PARTITION_RANGE));
        // Ends beyond the lookahead window, so nothing has to be created either
        PartitionInfoDto current = partition("detection_p_current",
                cutoff.minus(PARTITION_RANGE), now.plus(PARTITION_RANGE.multipliedBy(2)));

        PartitionDdlProvisioner ddlProvisioner = mock(PartitionDdlProvisioner.class);
        when(ddlProvisioner.isPartitioningSupported()).thenReturn(true);
        when(ddlProvisioner.fetchPartitions(PARENT_TABLE)).thenReturn(List.of(expired, current));
        RecordingPartitionService service = serviceWithProvisioner(ddlProvisioner);

        service.init();
        service.runPartitionMaintenance();

        verify(ddlProvisioner).dropPartition(expired.getName());
        verify(ddlProvisioner, never()).dropPartition(current.getName());
        // Creation resumes at the end of the newest partition, which already covers the lookahead
        verify(ddlProvisioner, never()).createPartitionIfMissing(anyString(), any(), any());
    }

    @Test
    public void testMaintenanceCreatesPartitionsStartingAtLastPartitionEnd() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        // Ends inside the lookahead window, so further partitions are needed
        OffsetDateTime lastEnd = now.plus(PARTITION_RANGE.dividedBy(2));
        PartitionInfoDto current = partition("detection_p_current", lastEnd.minus(PARTITION_RANGE), lastEnd);

        PartitionDdlProvisioner ddlProvisioner = mock(PartitionDdlProvisioner.class);
        when(ddlProvisioner.isPartitioningSupported()).thenReturn(true);
        when(ddlProvisioner.fetchPartitions(PARENT_TABLE)).thenReturn(List.of(current));
        RecordingPartitionService service = serviceWithProvisioner(ddlProvisioner);

        service.init();
        service.runPartitionMaintenance();

        ArgumentCaptor<OffsetDateTime> from = ArgumentCaptor.forClass(OffsetDateTime.class);
        verify(ddlProvisioner, atLeastOnce()).createPartitionIfMissing(eq(PARENT_TABLE), from.capture(), any());

        assertEquals(lastEnd, from.getAllValues().get(0), "new partitions must continue at the last partition end");
        verify(ddlProvisioner, never()).dropPartition(anyString());
    }
}
