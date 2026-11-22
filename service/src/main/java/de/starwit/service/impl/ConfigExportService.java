package de.starwit.service.impl;

import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.LinkEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.service.dto.ConfigExportDto;
import de.starwit.service.dto.ConfigExportDto.ClassificationExport;
import de.starwit.service.dto.ConfigExportDto.ImageExport;
import de.starwit.service.dto.ConfigExportDto.LinkExport;
import de.starwit.service.dto.ConfigExportDto.ObservationAreaExport;
import de.starwit.service.dto.ConfigExportDto.RegionExport;
import jakarta.transaction.Transactional;

/**
 * Builds a complete, self-contained export of all observation area
 * configurations (areas, camera, geo-referencing, links and regions).
 */
@Service
public class ConfigExportService {

    @Autowired
    private ObservationAreaService observationAreaService;

    @Autowired
    private ClassificationService classificationService;

    /**
     * @param areaIds ids of the observation areas to export; null or empty exports all
     */
    @Transactional
    public ConfigExportDto export(Collection<Long> areaIds) {
        Set<Long> selectedIds = areaIds == null ? Set.of() : Set.copyOf(areaIds);
        List<ClassificationExport> classifications = classificationService.findAll().stream()
                .sorted(Comparator.comparing(ClassificationEntity::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(c -> new ClassificationExport(c.getName(), c.getToolType(), c.getColor()))
                .toList();

        List<ObservationAreaExport> areas = observationAreaService.findAll().stream()
                .filter(area -> selectedIds.isEmpty() || selectedIds.contains(area.getId()))
                .map(this::toExport)
                .toList();

        return new ConfigExportDto(ConfigExportDto.FORMAT_VERSION, Instant.now(), classifications, areas);
    }

    private ObservationAreaExport toExport(ObservationAreaEntity area) {
        return new ObservationAreaExport(
                area.getId(),
                area.getName(),
                area.getCamera() != null ? area.getCamera().getSaeStreamKey() : null,
                area.getCamera() != null ? area.getCamera().isRecordingEnabled() : null,
                area.getProcessingEnabled(),
                area.getGeoReferenced(),
                area.getCenterlatitude(),
                area.getCenterlongitude(),
                area.getTopleftlatitude(),
                area.getTopleftlongitude(),
                area.getDegreeperpixelx(),
                area.getDegreeperpixely(),
                area.getImageWidth(),
                area.getImageHeight(),
                toImage(area.getImage()),
                toLinks(area.getLinks()),
                toRegions(area.getPolygon()));
    }

    private ImageExport toImage(ImageEntity image) {
        if (image == null || image.getData() == null) {
            return null;
        }
        return new ImageExport(
                image.getName(),
                image.getType(),
                Base64.getEncoder().encodeToString(image.getData()));
    }

    private List<LinkExport> toLinks(Set<LinkEntity> links) {
        if (links == null) {
            return List.of();
        }
        return links.stream()
                .sorted(Comparator.comparing(LinkEntity::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(l -> new LinkExport(l.getName(), l.getUrl()))
                .toList();
    }

    private List<RegionExport> toRegions(Set<PolygonEntity> polygons) {
        if (polygons == null) {
            return List.of();
        }
        return polygons.stream()
                .sorted(Comparator.comparing(PolygonEntity::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toRegion)
                .toList();
    }

    private RegionExport toRegion(PolygonEntity polygon) {
        List<PointEntity> points = polygon.getPoint() == null ? List.of() : polygon.getPoint();
        return new RegionExport(
                polygon.getName(),
                points.size() == 2 ? "line" : "polygon",
                polygon.getClassification() != null ? polygon.getClassification().getName() : null,
                polygon.getOpen(),
                polygon.getDirection() != null ? polygon.getDirection().name() : null,
                points.stream()
                        // points are persisted in drawing order, so id order is drawing order
                        .sorted(Comparator.comparing(PointEntity::getId,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(p -> List.of(p.getXvalue(), p.getYvalue()))
                        .toList());
    }
}
