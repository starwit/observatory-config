package de.starwit.service.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.ObservationAreaRepository;
import de.starwit.service.dto.DatabackendDto;
import de.starwit.service.dto.GeometryPointsDto;
import jakarta.transaction.Transactional;

@Service
public class DatabackendService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private URI databackendUri;

    private RestClient restClient;

    @Autowired
    private ObservationAreaRepository observationAreaRepository;

    public DatabackendService(@Value("${databackend.url}") URI configuredUri) {
        restClient = RestClient.create();
        // This is a workaround to make sure the URI ends in a "/", s.t. resolve() works
        // properly further down
        this.databackendUri = URI.create(configuredUri.toString() + "/").resolve("");
    }

    @Async
    @Transactional
    public void triggerConfigurationSync() {
        // Delete all and write all configs again for now, because of robustness
        List<ObservationAreaEntity> allObservationAreas = observationAreaRepository.findAll();

        log.info("Syncing configuration.");
        
        try {
            ResponseEntity<Void> deleteResponse = restClient.delete()
                    .uri(databackendUri.resolve("api/analytics-job/all"))
                    .retrieve().toBodilessEntity();

            log.debug("Cleared databackend configuration: HTTP " + deleteResponse.getStatusCode());
        } catch (RestClientResponseException | ResourceAccessException ex) {
            log.error("Could not clear existing jobs on databackend", ex);
            return;
        }

        for (ObservationAreaEntity observationArea : allObservationAreas) {
            sendConfig(observationArea);
        }
        
    }

    public void sendConfig(ObservationAreaEntity observationAreaEntity) {
        for (PolygonEntity polygon : observationAreaEntity.getPolygon()) {
            try {
                DatabackendDto dto = toDatabackendDto(observationAreaEntity, polygon);

                ResponseEntity<Void> postResponse = restClient.post()
                        .uri(databackendUri.resolve("api/analytics-job"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(dto)
                        .retrieve().toBodilessEntity();

                log.debug("Successfully sent configuration to databackend: HTTP " + postResponse.getStatusCode());
            } catch (IllegalGeometryException e) {
                log.error("Illegal geometry (needs to have either exactly 2 or more than 2 points)");
            } catch (RestClientResponseException | ResourceAccessException ex) {
                log.error("Could not send configuration to databackend", ex);
            }
        }
    }

    DatabackendDto toDatabackendDto(ObservationAreaEntity observationAreaEntity, PolygonEntity polygonEntity) throws IllegalGeometryException {
        DatabackendDto dbeDto = new DatabackendDto();

        dbeDto.setName(polygonEntity.getName());
        dbeDto.setCameraId(observationAreaEntity.getCamera().get(0).getSaeId());
        dbeDto.setDetectionClassId(2);
        dbeDto.setEnabled(true);
        dbeDto.setObservationAreaId(observationAreaEntity.getId());
        dbeDto.setClassification(polygonEntity.getClassification().getName());
        dbeDto.setGeoReferenced(observationAreaEntity.getGeoReferenced());

        List<GeometryPointsDto> geometryPoints = new ArrayList<>();

        if (polygonEntity.getPoint().size() == 2) {

            dbeDto.setType("LINE_CROSSING");
            geometryPoints.add(createGeometryPoint(polygonEntity, observationAreaEntity, 0));
            geometryPoints.add(createGeometryPoint(polygonEntity, observationAreaEntity, 1));

        } else if (polygonEntity.getPoint().size() > 2) {

            dbeDto.setType("AREA_OCCUPANCY");
            for (int i = 0; i < polygonEntity.getPoint().size(); i++) {
                geometryPoints.add(createGeometryPoint(polygonEntity, observationAreaEntity, i));
            }

        } else {
            throw new IllegalGeometryException();
        }

        dbeDto.setGeometryPoints(geometryPoints);

        return dbeDto;
    }

    private static GeometryPointsDto createGeometryPoint(PolygonEntity polygon, ObservationAreaEntity observationArea, int orderIdx) {
        GeometryPointsDto point = new GeometryPointsDto();
        point.setOrderIdx(orderIdx);

        BigDecimal xValue = polygon.getPoint().get(orderIdx).getXvalue();
        BigDecimal yValue = polygon.getPoint().get(orderIdx).getYvalue();
        BigDecimal xPixels = xValue.multiply(BigDecimal.valueOf(observationArea.getImageWidth()));
        BigDecimal yPixels = yValue.multiply(BigDecimal.valueOf(observationArea.getImageHeight()));

        if (observationArea.getGeoReferenced()) {
            point.setLatitude(observationArea.getTopleftlatitude().add(observationArea.getDegreeperpixely().multiply(yPixels)));
            point.setLongitude(observationArea.getTopleftlongitude().add(observationArea.getDegreeperpixelx().multiply(xPixels)));
        } else {
            point.setX(xValue.doubleValue());
            point.setY(yValue.doubleValue());
        }
        
        return point;
    }

    class IllegalGeometryException extends Exception {
    };
}
