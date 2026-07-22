package de.starwit.persistence.repository;

import java.sql.DatabaseMetaData;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class RecordingPartitionService {
    
    private static final Logger log = LoggerFactory.getLogger(RecordingPartitionService.class);

    private static final Pattern RANGE_PATTERN =
            Pattern.compile("FOR VALUES FROM \\('(.+?)'\\) TO \\('(.+?)'\\)");

    private static final DateTimeFormatter PG_TIMESTAMPTZ = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 1, 6, true)
            .optionalEnd()
            .appendOffset("+HH:mm:ss", "+00")
            .toFormatter();

    private static final DateTimeFormatter PARTITION_SUFFIX_FORMAT =
            DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    @Value("${partition.management.retentionTime:1d}")
    private Duration RETENTION_TIME;
    
    @Value("${partition.management.partitionRangeDuration:1d}")
    private Duration PARTITION_RANGE;

    @Value("${partition.management.partitionInitTimeUTC:00:00}")
    private LocalTime PARTITION_INIT_TIME_UTC;

    private static final String PARENT_TABLE = "detection";

    private static final String POSTGRESQL_PRODUCT_NAME = "PostgreSQL";

    private final JdbcTemplate jdbcTemplate;

    // Declarative partitioning is PostgreSQL-specific, so it is skipped on any other
    // database (e.g. the H2 instance used in tests)
    private boolean partitioningSupported;

    public RecordingPartitionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        partitioningSupported = isPostgreSql();
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

    private boolean isPostgreSql() {
        try {
            String productName = JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(),
                    DatabaseMetaData::getDatabaseProductName);
            return POSTGRESQL_PRODUCT_NAME.equalsIgnoreCase(productName);
        } catch (Exception e) {
            log.error("Could not determine database product - assuming partitioning is unsupported", e);
            return false;
        }
    }

    // --- Partition creation ---------------------------------------------

    public void ensureFuturePartitions() {
        // Determine end bound of last existing partition
        Optional<OffsetDateTime> lastPartitionEnd = lastPartitionEnd();
        log.debug("Last partition ends at {}", lastPartitionEnd);
        
        // Use a new init time if there is no partition yet
        OffsetDateTime nextPartitionStart = lastPartitionEnd.orElse(calcPartitionInit(Instant.now()));

        while (nextPartitionStart.isBefore(OffsetDateTime.now(ZoneOffset.UTC).plus(PARTITION_RANGE.multipliedBy(2)))) {
            OffsetDateTime from = nextPartitionStart;
            OffsetDateTime to = from.plus(PARTITION_RANGE);
            createPartitionIfMissing(from, to);

            nextPartitionStart = to;
        }
   
    }

    private Optional<OffsetDateTime> lastPartitionEnd() {
        List<PartitionInfo> existingPartitions = fetchPartitions(PARENT_TABLE);

        return existingPartitions.stream()
            .map(p -> p.toBound())
            .filter(Objects::nonNull)
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

    private void createPartitionIfMissing(OffsetDateTime from, OffsetDateTime to) {
        String partitionName = PARENT_TABLE + "_p" + from.format(PARTITION_SUFFIX_FORMAT);

        String sql = String.format("""
                CREATE TABLE IF NOT EXISTS "%s"
                PARTITION OF "%s"
                FOR VALUES FROM ('%s') TO ('%s')
                """,
                partitionName,
                PARENT_TABLE,
                from,
                to);

        try {
            log.info("Creating partition {} for range [{}, {})", partitionName, from, to);
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            log.error("Failed to create partition {} for range [{}, {})", partitionName, from, to, e);
            throw e;
        }
    }

    // --- Partition deletion ----------------------------------------------

    public void dropExpiredPartitions() {
        OffsetDateTime cutoff = OffsetDateTime.now().minus(RETENTION_TIME);

        List<PartitionInfo> partitions = fetchPartitions(PARENT_TABLE);

        for (PartitionInfo partition : partitions) {
            if (partition == null) continue;
            if (partition.toBound().isBefore(cutoff)) {
                log.info("Dropping partition with range FROM {} TO {}", partition.fromBound(), partition.toBound());
                dropPartition(partition.name());
            }
        }
    }

    private List<PartitionInfo> fetchPartitions(String parentTable) {
        String sql = """
                SELECT
                    child.relname AS partition_name,
                    pg_get_expr(child.relpartbound, child.oid) AS partition_expression
                FROM pg_inherits
                JOIN pg_class parent ON pg_inherits.inhparent = parent.oid
                JOIN pg_class child  ON pg_inherits.inhrelid  = child.oid
                WHERE parent.relname = ?
                """;

        List<PartitionInfo> partitions = jdbcTemplate.query(sql, (rs, rowNum) ->
                parsePartitionInfo(rs.getString("partition_name"), rs.getString("partition_expression")),
                parentTable);

        for (PartitionInfo partition : partitions) {
            log.debug("Found existing partition: {}", partition);
        }

        return partitions;
    }

    private PartitionInfo parsePartitionInfo(String name, String expr) {
        Matcher matcher = RANGE_PATTERN.matcher(expr);
        if (!matcher.find()) {
            log.warn("Skipping partition {} not matching expression: {}", name, expr);
            return null;
        }
        String rawFrom = matcher.group(1);
        String rawTo = matcher.group(2);
        OffsetDateTime from = OffsetDateTime.parse(rawFrom, PG_TIMESTAMPTZ).withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.parse(rawTo, PG_TIMESTAMPTZ).withOffsetSameInstant(ZoneOffset.UTC);

        return new PartitionInfo(name, from, to, rawFrom, rawTo);
    }

    private void dropPartition(String partitionName) {
        jdbcTemplate.execute("DROP TABLE IF EXISTS \"" + partitionName + "\"");
    }

    private record PartitionInfo(String name, OffsetDateTime fromBound, OffsetDateTime toBound, String rawFromBound, String rawToBound) {}    
}
