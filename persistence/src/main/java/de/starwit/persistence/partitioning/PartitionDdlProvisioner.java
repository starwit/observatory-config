package de.starwit.persistence.partitioning;

import java.sql.DatabaseMetaData;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

/**
 * Executes the DDL statements needed to manage declarative table partitions and
 * reads the currently existing partitions from the database catalog.
 */
@Component
public class PartitionDdlProvisioner {

    private static final Logger log = LoggerFactory.getLogger(PartitionDdlProvisioner.class);

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

    private static final String POSTGRESQL_PRODUCT_NAME = "PostgreSQL";

    private final JdbcTemplate jdbcTemplate;

    public PartitionDdlProvisioner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Declarative partitioning is PostgreSQL-specific, so it has to be skipped on any
     * other database (e.g. the H2 instance used in tests)
     */
    public boolean isPartitioningSupported() {
        try {
            String productName = JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(),
                    DatabaseMetaData::getDatabaseProductName);
            return POSTGRESQL_PRODUCT_NAME.equalsIgnoreCase(productName);
        } catch (Exception e) {
            log.error("Could not determine database product - assuming partitioning is unsupported", e);
            return false;
        }
    }

    public List<PartitionInfoDto> fetchPartitions(String parentTable) {
        String sql = """
                SELECT
                    child.relname AS partition_name,
                    pg_get_expr(child.relpartbound, child.oid) AS partition_expression
                FROM pg_inherits
                JOIN pg_class parent ON pg_inherits.inhparent = parent.oid
                JOIN pg_class child  ON pg_inherits.inhrelid  = child.oid
                WHERE parent.relname = ?
                """;

        List<PartitionInfoDto> partitions = jdbcTemplate.query(sql, (rs, rowNum) ->
                parsePartitionInfo(rs.getString("partition_name"), rs.getString("partition_expression")),
                parentTable);

        for (PartitionInfoDto partition : partitions) {
            log.debug("Found existing partition: {}", partition);
        }

        return partitions.stream().filter(Objects::nonNull).toList();
    }

    public void createPartitionIfMissing(String parentTable, OffsetDateTime from, OffsetDateTime to) {
        String partitionName = parentTable + "_p" + from.format(PARTITION_SUFFIX_FORMAT);

        String sql = String.format("""
                CREATE TABLE IF NOT EXISTS "%s"
                PARTITION OF "%s"
                FOR VALUES FROM ('%s') TO ('%s')
                """,
                partitionName,
                parentTable,
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

    public void dropPartition(String partitionName) {
        jdbcTemplate.execute("DROP TABLE IF EXISTS \"" + partitionName + "\"");
    }

    private PartitionInfoDto parsePartitionInfo(String name, String expr) {
        Matcher matcher = RANGE_PATTERN.matcher(expr);
        if (!matcher.find()) {
            log.warn("Skipping partition {} not matching expression: {}", name, expr);
            return null;
        }
        String rawFrom = matcher.group(1);
        String rawTo = matcher.group(2);
        OffsetDateTime from = OffsetDateTime.parse(rawFrom, PG_TIMESTAMPTZ).withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.parse(rawTo, PG_TIMESTAMPTZ).withOffsetSameInstant(ZoneOffset.UTC);

        return new PartitionInfoDto(name, from, to, rawFrom, rawTo);
    }
}
