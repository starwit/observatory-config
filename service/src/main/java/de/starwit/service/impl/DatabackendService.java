package de.starwit.service.impl;

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
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import de.starwit.service.dto.DatabackendDto;
import de.starwit.service.dto.GeometryPointsDto;
import de.starwit.service.dto.ImageDto;
import de.starwit.service.dto.RegionDto;

@Service
public class DatabackendService {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Value("${databackend.url}")
    private URI databackendUri;

    private RestClient restClient;

    public DatabackendService() {
        restClient = RestClient.create();
    }

    @Async
    public void syncConfiguration(ImageDto imageDto) {
        try {
            ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri(databackendUri.resolve("api/analytics-job/all"))
                .retrieve().toBodilessEntity();
            
            log.info("Cleared databackend configuration: HTTP " + deleteResponse.getStatusCode());
        } catch (RestClientResponseException ex) {
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
            } catch (RestClientResponseException ex) {
                log.error("Could not send configuration to databackend", ex);
            }
        }
    }

    private DatabackendDto toDatabackendDto(ImageDto imageDto, RegionDto regionDto) throws IllegalGeometryException {
        DatabackendDto dbeDto = new DatabackendDto();

        dbeDto.setName("config");
        dbeDto.setCameraId(imageDto.getName());
        dbeDto.setDetectionClassId(2);
        dbeDto.setEnabled(true);
        dbeDto.setParkingAreaId(1L);

        if (regionDto.getPoints().size() == 2) {
            dbeDto.setType("LINE_CROSSING");
        } else if (regionDto.getPoints().size() > 2) {
            dbeDto.setType("AREA_OCCUPANCY");
        } else {
            throw new IllegalGeometryException();
        }

        List<GeometryPointsDto> geometryPoints = new ArrayList<>();
        for (int i = 0; i < regionDto.getPoints().size(); i++) {
            GeometryPointsDto point = new GeometryPointsDto();
            point.setX(regionDto.getPoints().get(i).get(0));            
            point.setY(regionDto.getPoints().get(i).get(1));            
            point.setOrderIdx(i);
            geometryPoints.add(point);
        }
        dbeDto.setGeometryPoints(geometryPoints);
        
        return dbeDto;
    }

    class IllegalGeometryException extends Exception {};
}
