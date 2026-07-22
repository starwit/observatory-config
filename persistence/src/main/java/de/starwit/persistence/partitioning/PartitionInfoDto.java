package de.starwit.persistence.partitioning;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Describes a single existing partition of a partitioned table.
 */
public class PartitionInfoDto {

    private final String name;
    private final OffsetDateTime fromBound;
    private final OffsetDateTime toBound;
    private final String rawFromBound;
    private final String rawToBound;

    /**
     * @param name          name of the partition table
     * @param fromBound     inclusive lower bound of the partition range (UTC)
     * @param toBound       exclusive upper bound of the partition range (UTC)
     * @param rawFromBound  lower bound as reported by the database
     * @param rawToBound    upper bound as reported by the database
     */
    public PartitionInfoDto(String name, OffsetDateTime fromBound, OffsetDateTime toBound,
            String rawFromBound, String rawToBound) {
        this.name = name;
        this.fromBound = fromBound;
        this.toBound = toBound;
        this.rawFromBound = rawFromBound;
        this.rawToBound = rawToBound;
    }

    public String getName() {
        return name;
    }

    public OffsetDateTime getFromBound() {
        return fromBound;
    }

    public OffsetDateTime getToBound() {
        return toBound;
    }

    public String getRawFromBound() {
        return rawFromBound;
    }

    public String getRawToBound() {
        return rawToBound;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PartitionInfoDto other)) {
            return false;
        }
        return Objects.equals(name, other.name)
                && Objects.equals(fromBound, other.fromBound)
                && Objects.equals(toBound, other.toBound)
                && Objects.equals(rawFromBound, other.rawFromBound)
                && Objects.equals(rawToBound, other.rawToBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fromBound, toBound, rawFromBound, rawToBound);
    }

    @Override
    public String toString() {
        return "PartitionInfo [name=" + name + ", fromBound=" + fromBound + ", toBound=" + toBound
                + ", rawFromBound=" + rawFromBound + ", rawToBound=" + rawToBound + "]";
    }
}
