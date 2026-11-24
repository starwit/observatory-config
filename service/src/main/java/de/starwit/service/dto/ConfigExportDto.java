package de.starwit.service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Self-contained JSON export of all observation area configurations.
 * Region points are normalized image coordinates (0..1), in drawing order.
 */
public record ConfigExportDto(
        int formatVersion,
        Instant exportedAt,
        List<ClassificationExport> classifications,
        List<ObservationAreaExport> observationAreas) {

    public static final int FORMAT_VERSION = 1;

    public record ClassificationExport(
            String name,
            String toolType,
            String color) {
    }

    public record ObservationAreaExport(
            Long id,
            String name,
            String saeStreamKey,
            Boolean recordingEnabled,
            Boolean processingEnabled,
            Boolean geoReferenced,
            BigDecimal centerlatitude,
            BigDecimal centerlongitude,
            BigDecimal topleftlatitude,
            BigDecimal topleftlongitude,
            BigDecimal degreeperpixelx,
            BigDecimal degreeperpixely,
            Integer imageWidth,
            Integer imageHeight,
            ImageExport image,
            List<LinkExport> links,
            List<RegionExport> regions) {
    }

    /**
     * Observation area background image; data is the raw image file, Base64 encoded.
     */
    public record ImageExport(
            String name,
            String type,
            String data) {
    }

    public record LinkExport(
            String name,
            String url) {
    }

    public record RegionExport(
            String name,
            String type,
            String classification,
            Boolean open,
            String direction,
            List<List<BigDecimal>> points) {
    }
}
