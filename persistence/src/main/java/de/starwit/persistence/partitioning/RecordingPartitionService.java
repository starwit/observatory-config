package de.starwit.persistence.partitioning;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Keeps the partitions of the detection table in shape by creating partitions ahead of
 * time and dropping those that fell out of the retention window.
 */
@Service
public class RecordingPartitionService {

    private static final Logger log = LoggerFactory.getLogger(RecordingPartitionService.class);

    private static final String PARENT_TABLE = "detection";

    @Value("${partition.management.retentionTime:1d}")
    private Duration RETENTION_TIME;

    @Value("${partition.management.partitionRangeDuration:1d}")
    private Duration PARTITION_RANGE;

    @Value("${partition.management.partitionInitTimeUTC:00:00}")
    private LocalTime PARTITION_INIT_TIME_UTC;

    @Autowired
    private PartitionDdlProvisioner ddlProvisioner;

    private boolean partitioningSupported;

    @PostConstruct
    public void init() {
        partitioningSupported = ddlProvisioner.isPartitioningSupported();
        if (!partitioningSupported) {
            log.warn("Database is not PostgreSQL - partition management is disabled");
            return;
        }

        // Make sure that partitions exist once at start
        // In abnormal database conditions this will throw and cancel application startup!
        ensureFuturePartitions();
    }

    @Scheduled(fixedRateString = "${partition.management.maintenanceInterval:1h}")
    public void runPartitionMaintenance() {
        if (!partitioningSupported) {
            return;
        }

        log.info("Running partition maintenance");
        // Drop first to increase chances of escaping a "file system full" condition
        dropExpiredPartitions();
        ensureFuturePartitions();
    }

    // --- Partition creation ---------------------------------------------

    public void ensureFuturePartitions() {
        // Determine end bound of last existing partition
        Optional<OffsetDateTime> lastPartitionEnd = lastPartitionEnd();
        log.debug("Last partition ends at {}", lastPartitionEnd);

        // Use a new init time if there is no partition yet
        OffsetDateTime nextPartitionStart = lastPartitionEnd.orElse(calcPartitionInit(Instant.now()));

        while (nextPartitionStart.isBefore(OffsetDateTime.now(ZoneOffset.UTC).plus(PARTITION_RANGE))) {
            OffsetDateTime from = nextPartitionStart;
            OffsetDateTime to = from.plus(PARTITION_RANGE);
            ddlProvisioner.createPartitionIfMissing(PARENT_TABLE, from, to);

            nextPartitionStart = to;
        }

    }

    private Optional<OffsetDateTime> lastPartitionEnd() {
        return ddlProvisioner.fetchPartitions(PARENT_TABLE).stream()
            .map(PartitionInfoDto::getToBound)
            .max(Comparator.naturalOrder());
    }

    OffsetDateTime calcPartitionInit(Instant now) {
        OffsetDateTime nowUtc = now.atOffset(ZoneOffset.UTC);
        if (nowUtc.toLocalTime().isBefore(PARTITION_INIT_TIME_UTC)) {
            return OffsetDateTime.of(nowUtc.toLocalDate().minusDays(1), PARTITION_INIT_TIME_UTC, ZoneOffset.UTC);
        } else {
            return OffsetDateTime.of(nowUtc.toLocalDate(), PARTITION_INIT_TIME_UTC, ZoneOffset.UTC);
        }
    }

    // --- Partition deletion ----------------------------------------------

    public void dropExpiredPartitions() {
        OffsetDateTime cutoff = OffsetDateTime.now().minus(RETENTION_TIME);

        List<PartitionInfoDto> partitions = ddlProvisioner.fetchPartitions(PARENT_TABLE);

        for (PartitionInfoDto partition : partitions) {
            if (partition.getToBound().isBefore(cutoff)) {
                log.info("Dropping partition with range FROM {} TO {}", partition.getFromBound(),
                        partition.getToBound());
                ddlProvisioner.dropPartition(partition.getName());
            }
        }
    }
}
