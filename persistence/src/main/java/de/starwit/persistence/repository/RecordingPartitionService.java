package de.starwit.persistence.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@ConditionalOnProperty(name = "partition.management.enabled", havingValue = "true", matchIfMissing = false)
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
            DateTimeFormatter.ofPattern("yyyy_MM_dd");

    @Value("${partition.management.retention.days:2}")
    private static final int RETENTION_DAYS = 2;
    private static final int LOOKAHEAD_DAYS = 1; // how many days ahead to pre-create
    private static final String PARENT_TABLE = "detection";

    @Value("${partition.management.timezone:UTC}")
    private String timezone;

    private final JdbcTemplate jdbcTemplate;

    public RecordingPartitionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private ZoneId zoneId() {
        return ZoneId.of(timezone);
    }

    @PostConstruct
    public void init() {
        // check if partition exist upon start, if not create
        ensureFuturePartitions();
    }

    // --- Partition creation ---------------------------------------------

    @Scheduled(cron = "0 0 1 * * *") // daily at 01:00, before the drop job
    public void ensureFuturePartitions() {
        LocalDate today = LocalDate.now();

        log.info("Check if new partitions need to be created.");

        // Create today's partition too, in case it's somehow missing
        for (int offset = 0; offset <= LOOKAHEAD_DAYS; offset++) {
            LocalDate day = today.plusDays(offset);
            createDailyPartitionIfMissing(day);
        }
    }

    private void createDailyPartitionIfMissing(LocalDate day) {
        ZonedDateTime from = day.atStartOfDay(zoneId());
        ZonedDateTime to = day.plusDays(1).atStartOfDay(zoneId());

        String partitionName = PARENT_TABLE + "_p" + day.format(PARTITION_SUFFIX_FORMAT);

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
            jdbcTemplate.execute(sql);
            log.info("Ensured partition {} exists for range [{}, {})", partitionName, from, to);
        } catch (Exception e) {
            log.error("Failed to create partition {} for range [{}, {})", partitionName, from, to, e);
        }
    }

    // --- Partition deletion ----------------------------------------------

    @Scheduled(cron = "0 * * * * *") // daily at 02:30
    public void dropOldPartitions() {
        LocalDateTime cutoff = LocalDateTime.now()
                .minusDays(RETENTION_DAYS);

        log.info("Check if old partitions need to be deleted.");

        List<PartitionInfo> partitions = fetchPartitions(PARENT_TABLE);

        for (PartitionInfo partition : partitions) {
            if (partition == null) continue;
            if (partition.toBound().isBefore(cutoff)) {
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

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                parsePartitionInfo(rs.getString("partition_name"), rs.getString("partition_expression")),
                parentTable);
    }

    private PartitionInfo parsePartitionInfo(String name, String expr) {
        Matcher matcher = RANGE_PATTERN.matcher(expr);
        if (!matcher.find()) {
            log.debug("Skipping partition {} with expression: {}", name, expr);
            return null;
        }
        LocalDateTime from = OffsetDateTime.parse(matcher.group(1), PG_TIMESTAMPTZ).atZoneSameInstant(zoneId()).toLocalDateTime();
        LocalDateTime to = OffsetDateTime.parse(matcher.group(2), PG_TIMESTAMPTZ).atZoneSameInstant(zoneId()).toLocalDateTime();
        return new PartitionInfo(name, from, to);
    }

    private void dropPartition(String partitionName) {
        log.info("Dropping old detection partition: {}", partitionName);
        jdbcTemplate.execute("DROP TABLE IF EXISTS \"" + partitionName + "\"");
    }

    private record PartitionInfo(String name, LocalDateTime fromBound, LocalDateTime toBound) {}    
}
