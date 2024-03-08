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

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.persistence.repository.ImageRepository;
import de.starwit.persistence.repository.ParkingAreaRepository;
import de.starwit.service.dto.DatabackendDto;
import de.starwit.service.dto.GeometryPointsDto;

@Service
public class DatabackendService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private URI databackendUri;

    private RestClient restClient;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

    public DatabackendService(@Value("${databackend.url}") URI configuredUri) {
        restClient = RestClient.create();
        // This is a workaround to make sure the URI ends in a "/", s.t. resolve() works
        // properly further down
        this.databackendUri = URI.create(configuredUri.toString() + "/").resolve("");
    }

    @Async
    public void triggerConfigurationSync(Long updatedImageId) {
        ImageEntity updatedImage = imageRepository.findById(updatedImageId).orElse(null);
        if (updatedImage == null) {
            log.warn("Could not find image for id " + updatedImageId);
            return;
        }
        
        try {
            ResponseEntity<Void> deleteResponse = restClient.delete()
                    .uri(databackendUri.resolve("api/analytics-job/all"))
                    .retrieve().toBodilessEntity();

            log.info("Cleared databackend configuration: HTTP " + deleteResponse.getStatusCode());
        } catch (RestClientResponseException | ResourceAccessException ex) {
            log.error("Could not clear existing jobs on databackend", ex);
            return;
        }

        for (PolygonEntity polygon : updatedImage.getPolygon()) {
            try {
                DatabackendDto dto = toDatabackendDto(updatedImage, polygon);

                ResponseEntity<Void> postResponse = restClient.post()
                        .uri(databackendUri.resolve("api/analytics-job"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(dto)
                        .retrieve().toBodilessEntity();

                log.info("Successfully sent configuration to databackend: HTTP " + postResponse.getStatusCode());
            } catch (IllegalGeometryException e) {
                log.error("Illegal geometry (needs to have either exactly 2 or more than 2 points)");
            } catch (RestClientResponseException | ResourceAccessException ex) {
                log.error("Could not send configuration to databackend", ex);
            }
        }
    }

    DatabackendDto toDatabackendDto(ImageEntity imageEntity, PolygonEntity polygonEntity) throws IllegalGeometryException {
        DatabackendDto dbeDto = new DatabackendDto();

        dbeDto.setName(polygonEntity.getName());
        dbeDto.setCameraId(imageEntity.getCamera().get(0).getSaeId());
        dbeDto.setDetectionClassId(2);
        dbeDto.setEnabled(true);
        dbeDto.setParkingAreaId(1L);
        dbeDto.setClassification(polygonEntity.getClassification().getName());
        dbeDto.setGeoReferenced(imageEntity.getGeoReferenced());

        List<GeometryPointsDto> geometryPoints = new ArrayList<>();

        if (polygonEntity.getPoint().size() == 2) {

            dbeDto.setType("LINE_CROSSING");
            geometryPoints.add(createGeometryPoint(polygonEntity, imageEntity, 0));
            geometryPoints.add(createGeometryPoint(polygonEntity, imageEntity, 1));

        } else if (polygonEntity.getPoint().size() > 2) {

            dbeDto.setType("AREA_OCCUPANCY");
            for (int i = 0; i < polygonEntity.getPoint().size(); i++) {
                geometryPoints.add(createGeometryPoint(polygonEntity, imageEntity, i));
            }

        } else {
            throw new IllegalGeometryException();
        }

        dbeDto.setGeometryPoints(geometryPoints);

        return dbeDto;
    }

    private static GeometryPointsDto createGeometryPoint(PolygonEntity polygon, ImageEntity image, int orderIdx) {
        GeometryPointsDto point = new GeometryPointsDto();
        point.setOrderIdx(orderIdx);

        BigDecimal xValue = polygon.getPoint().get(orderIdx).getXvalue();
        BigDecimal yValue = polygon.getPoint().get(orderIdx).getYvalue();

        if (image.getGeoReferenced()) {
            point.setLatitude(image.getTopleftlatitude().add(image.getDegreeperpixelx().multiply(xValue)));
            point.setLongitude(image.getTopleftlongitude().add(image.getDegreeperpixely().multiply(yValue)));
        } else {
            point.setX(xValue.doubleValue());
            point.setY(yValue.doubleValue());
        }
        
        return point;
    }

    class IllegalGeometryException extends Exception {
    };
}
