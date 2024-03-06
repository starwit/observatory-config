package de.starwit.service.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import de.starwit.service.dto.DatabackendDto;
import de.starwit.service.dto.GeometryPointsDto;
import de.starwit.service.dto.ImageDto;
import de.starwit.service.dto.RegionDto;

@Service
public class DatabackendService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private URI databackendUri;

    private RestClient restClient;

    private String cameraId;

    public DatabackendService(@Value("${databackend.url}") URI configuredUri, @Value("${databackend.cameraId}") String cameraId) {
        restClient = RestClient.create();
        // This is a workaround to make sure the URI ends in a "/", s.t. resolve() works
        // properly further down
        this.databackendUri = URI.create(configuredUri.toString() + "/").resolve("");
        this.cameraId = cameraId;
    }

    @Async
    public void syncConfiguration(ImageDto imageDto) {
        try {
            ResponseEntity<Void> deleteResponse = restClient.delete()
                    .uri(databackendUri.resolve("api/analytics-job/all"))
                    .retrieve().toBodilessEntity();

            log.info("Cleared databackend configuration: HTTP " + deleteResponse.getStatusCode());
        } catch (RestClientResponseException | ResourceAccessException ex) {
            log.error("Could not clear existing jobs on databackend", ex);
            return;
        }

        for (RegionDto regionDto : imageDto.getRegions()) {
            try {
                DatabackendDto dto = toDatabackendDto(imageDto, regionDto);

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

    DatabackendDto toDatabackendDto(ImageDto imageDto, RegionDto regionDto) throws IllegalGeometryException {
        DatabackendDto dbeDto = new DatabackendDto();

        dbeDto.setName(regionDto.getName());
        dbeDto.setCameraId(cameraId);
        dbeDto.setDetectionClassId(2);
        dbeDto.setEnabled(true);
        dbeDto.setParkingAreaId(1L);
        dbeDto.setClassification(regionDto.getCls());

        List<GeometryPointsDto> geometryPoints = new ArrayList<>();

        if (regionDto.getType().equals("line")) {

            dbeDto.setType("LINE_CROSSING");
            GeometryPointsDto point1 = new GeometryPointsDto();
            point1.setX(regionDto.getX1() * imageDto.getImageWidth());
            point1.setY(regionDto.getY1() * imageDto.getImageHeight());
            point1.setLongitude(imageDto.getTopleftlongitude().add(imageDto.getDegreeperpixelx().multiply(BigDecimal.valueOf(point1.getX()))));
            point1.setLatitude(imageDto.getTopleftlatitude().add(imageDto.getDegreeperpixely().multiply(BigDecimal.valueOf(point1.getY()))));
            point1.setOrderIdx(0);
            GeometryPointsDto point2 = new GeometryPointsDto();
            point2.setX(regionDto.getX2() * imageDto.getImageWidth());
            point2.setY(regionDto.getY2() * imageDto.getImageHeight());
            point2.setOrderIdx(1);
            geometryPoints.add(point1);
            geometryPoints.add(point2);

        } else if (regionDto.getType().equals("polygon")) {

            dbeDto.setType("AREA_OCCUPANCY");
            for (int i = 0; i < regionDto.getPoints().size(); i++) {
                GeometryPointsDto point = new GeometryPointsDto();
                point.setX(regionDto.getPoints().get(i).get(0) * imageDto.getImageWidth());
                point.setY(regionDto.getPoints().get(i).get(1) * imageDto.getImageHeight());
                point.setOrderIdx(i);
                geometryPoints.add(point);
            }

        } else {
            throw new IllegalGeometryException();
        }

        dbeDto.setGeometryPoints(geometryPoints);

        return dbeDto;
    }

    class IllegalGeometryException extends Exception {
    };
}
