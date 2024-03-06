package de.starwit.service.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import de.starwit.service.dto.DatabackendDto;
import de.starwit.service.dto.ImageDto;
import de.starwit.service.dto.RegionDto;
import de.starwit.service.impl.DatabackendService.IllegalGeometryException;

public class DatabackendServiceTest {
    
    @Test
    public void testToDatabackendDtoPixelBasedLine() throws IllegalGeometryException {

        RegionDto lineDto = createLineWithDefaults();
        
        ImageDto imageDto = createImageDtoWithDefaults();
        imageDto.setGeoReferenced(false);
        imageDto.setRegions(Arrays.asList(lineDto));
        
        DatabackendService testee = new DatabackendService(URI.create("http://localhost"), "stream1");

        DatabackendDto dbeDto = testee.toDatabackendDto(imageDto, imageDto.getRegions().get(0));

        assertThat(dbeDto.getCameraId()).isEqualTo("stream1");
        assertThat(dbeDto.getClassification()).isEqualTo(lineDto.getCls());
        assertThat(dbeDto.getDetectionClassId()).isEqualTo(2);
        assertThat(dbeDto.getEnabled()).isTrue();
        assertThat(dbeDto.getName()).isEqualTo("lineRegion");
        assertThat(dbeDto.getParkingAreaId()).isEqualTo(1);
        assertThat(dbeDto.getType()).isEqualTo("LINE_CROSSING");        
    }

    RegionDto createLineWithDefaults() {
        RegionDto lineRegionDto = new RegionDto();

        lineRegionDto.setId("id");
        lineRegionDto.setType("line");
        lineRegionDto.setCls("Line");
        lineRegionDto.setName("lineRegion");
        lineRegionDto.setOpen(true);
        lineRegionDto.setColor("#123456");
        lineRegionDto.setX1(0);
        lineRegionDto.setY1(0);
        lineRegionDto.setX2(10);
        lineRegionDto.setY2(10);
        
        return lineRegionDto;
    }

    RegionDto createPolygonWithDefaults() {
        RegionDto polygonRegionDto = new RegionDto();

        polygonRegionDto.setId("id2");
        polygonRegionDto.setType("polygon");
        polygonRegionDto.setCls("Area");
        polygonRegionDto.setName("polygonRegion");
        polygonRegionDto.setOpen(false);
        polygonRegionDto.setColor("#123456");

        List<List<Double>> points = Arrays.asList(
            Arrays.asList(0.0, 0.0),
            Arrays.asList(10.0, 0.0),
            Arrays.asList(0.0, 10.0)
        );
        polygonRegionDto.setPoints(points);
        
        return polygonRegionDto;
    }
    
    ImageDto createImageDtoWithDefaults() {
        ImageDto imageDto = new ImageDto();

        imageDto.setId(1L);
        imageDto.setName("testImage");
        imageDto.setType("testType");
        imageDto.setImageHeight(1000);
        imageDto.setImageWidth(2000);
        imageDto.setDegreeperpixelx(new BigDecimal(0.001));
        imageDto.setDegreeperpixely(new BigDecimal(0.001));
        imageDto.setGeoReferenced(false);
        imageDto.setTopleftlongitude(new BigDecimal(10));
        imageDto.setTopleftlatitude(new BigDecimal(52));

        return imageDto;
    }
}
