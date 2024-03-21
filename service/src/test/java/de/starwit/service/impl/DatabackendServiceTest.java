package de.starwit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.starwit.persistence.entity.CameraEntity;
import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.persistence.entity.ImageEntity;
import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.service.dto.DatabackendDto;
import de.starwit.service.impl.DatabackendService.IllegalGeometryException;

public class DatabackendServiceTest {
    
    @Test
    public void testToDatabackendDtoPixelLine() throws IllegalGeometryException {

        PolygonEntity polygon = createLineWithDefaults();
        
        ImageEntity image = createImageWithDefaults();
        image.setGeoReferenced(false);
        
        DatabackendService testee = new DatabackendService(URI.create("http://localhost"));

        DatabackendDto dbeDto = testee.toDatabackendDto(image, polygon);

        assertThat(dbeDto.getCameraId()).isEqualTo("stream1");
        assertThat(dbeDto.getClassification()).isEqualTo(polygon.getClassification().getName());
        assertThat(dbeDto.getDetectionClassId()).isEqualTo(2);
        assertThat(dbeDto.getEnabled()).isTrue();
        assertThat(dbeDto.getName()).isEqualTo("lineRegion");
        assertThat(dbeDto.getObservationAreaId()).isEqualTo(1);
        assertThat(dbeDto.getType()).isEqualTo("LINE_CROSSING");
        assertThat(dbeDto.getGeoReferenced()).isFalse();
        assertThat(dbeDto.getGeometryPoints().get(0).getX()).isEqualTo(0);
        assertThat(dbeDto.getGeometryPoints().get(0).getLatitude()).isNull();
    }

    @Test
    public void testToDatabackendDtoGeoLine() throws IllegalGeometryException {

        PolygonEntity polygon = createLineWithDefaults();
        
        ImageEntity image = createImageWithDefaults();
        image.setGeoReferenced(true);
        
        DatabackendService testee = new DatabackendService(URI.create("http://localhost"));

        DatabackendDto dbeDto = testee.toDatabackendDto(image, polygon);

        assertThat(dbeDto.getCameraId()).isEqualTo("stream1");
        assertThat(dbeDto.getClassification()).isEqualTo(polygon.getClassification().getName());
        assertThat(dbeDto.getDetectionClassId()).isEqualTo(2);
        assertThat(dbeDto.getEnabled()).isTrue();
        assertThat(dbeDto.getName()).isEqualTo("lineRegion");
        assertThat(dbeDto.getObservationAreaId()).isEqualTo(1);
        assertThat(dbeDto.getType()).isEqualTo("LINE_CROSSING");
        assertThat(dbeDto.getGeoReferenced()).isTrue();  
        assertThat(dbeDto.getGeometryPoints().get(1).getX()).isNull();;
        assertThat(dbeDto.getGeometryPoints().get(1).getLatitude().doubleValue()).isEqualTo(52.5);
    }

    @Test
    public void testToDatabackendDtoPixelPolygon() throws IllegalGeometryException {

        PolygonEntity polygon = createPolygonWithDefaults();
        
        ImageEntity image = createImageWithDefaults();
        image.setGeoReferenced(false);
        
        DatabackendService testee = new DatabackendService(URI.create("http://localhost"));

        DatabackendDto dbeDto = testee.toDatabackendDto(image, polygon);

        assertThat(dbeDto.getCameraId()).isEqualTo("stream1");
        assertThat(dbeDto.getClassification()).isEqualTo(polygon.getClassification().getName());
        assertThat(dbeDto.getDetectionClassId()).isEqualTo(2);
        assertThat(dbeDto.getEnabled()).isTrue();
        assertThat(dbeDto.getName()).isEqualTo("polygonRegion");
        assertThat(dbeDto.getObservationAreaId()).isEqualTo(1);
        assertThat(dbeDto.getType()).isEqualTo("AREA_OCCUPANCY");
        assertThat(dbeDto.getGeoReferenced()).isFalse();
        assertThat(dbeDto.getGeometryPoints().get(2).getY()).isEqualTo(0.5);
        assertThat(dbeDto.getGeometryPoints().get(2).getLatitude()).isNull();
    }

    @Test
    public void testToDatabackendDtoGeoPolygon() throws IllegalGeometryException {

        PolygonEntity polygon = createPolygonWithDefaults();
        
        ImageEntity image = createImageWithDefaults();
        image.setGeoReferenced(true);
        
        DatabackendService testee = new DatabackendService(URI.create("http://localhost"));

        DatabackendDto dbeDto = testee.toDatabackendDto(image, polygon);

        assertThat(dbeDto.getCameraId()).isEqualTo("stream1");
        assertThat(dbeDto.getClassification()).isEqualTo(polygon.getClassification().getName());
        assertThat(dbeDto.getDetectionClassId()).isEqualTo(2);
        assertThat(dbeDto.getEnabled()).isTrue();
        assertThat(dbeDto.getName()).isEqualTo("polygonRegion");
        assertThat(dbeDto.getObservationAreaId()).isEqualTo(1);
        assertThat(dbeDto.getType()).isEqualTo("AREA_OCCUPANCY");
        assertThat(dbeDto.getGeoReferenced()).isTrue();
        assertThat(dbeDto.getGeometryPoints().get(2).getY()).isNull();
        assertThat(dbeDto.getGeometryPoints().get(2).getLongitude().doubleValue()).isEqualTo(10.0);
        assertThat(dbeDto.getGeometryPoints().get(2).getLatitude().doubleValue()).isEqualTo(52.5);
    }

    PolygonEntity createLineWithDefaults() {
        ClassificationEntity cls = new ClassificationEntity();
        cls.setName("testClassification");

        PolygonEntity polygon = new PolygonEntity();

        polygon.setId(1L);
        polygon.setName("lineRegion");
        polygon.setOpen(true);
        polygon.setClassification(cls);
    
        polygon.setPoint(Arrays.asList(
            createPoint(1, 0, 0),
            createPoint(2, 0.5, 0.5)
        )); 

        return polygon;
    }

    PolygonEntity createPolygonWithDefaults() {
        ClassificationEntity cls = new ClassificationEntity();
        cls.setName("testClassification");
        
        PolygonEntity polygon = new PolygonEntity();

        polygon.setId(2L);
        polygon.setName("polygonRegion");
        polygon.setOpen(false);
        polygon.setClassification(cls);

        polygon.setPoint(Arrays.asList(
            createPoint(1, 0, 0),
            createPoint(2, 0.5, 0),
            createPoint(3, 0, 0.5)
        ));

        return polygon;
    }
    
    ImageEntity createImageWithDefaults() {
        CameraEntity camera = new CameraEntity();
        camera.setSaeId("stream1");

        ObservationAreaEntity observationArea = new ObservationAreaEntity();
        observationArea.setId(1L);


        ImageEntity image = new ImageEntity();

        image.setId(1L);
        image.setName("testImage");
        image.setType("testType");
        image.setImageHeight(1000);
        image.setImageWidth(1000);
        image.setDegreeperpixelx(new BigDecimal(0.001));
        image.setDegreeperpixely(new BigDecimal(0.001));
        image.setGeoReferenced(false);
        image.setTopleftlongitude(new BigDecimal(10));
        image.setTopleftlatitude(new BigDecimal(52));
        image.setCamera(Arrays.asList(camera));
        image.setObservationArea(observationArea);

        return image;
    }

    PointEntity createPoint(int id, double x, double y) {
        PointEntity point = new PointEntity();
        point.setId((long) id);
        point.setXvalue(BigDecimal.valueOf(x));
        point.setYvalue(BigDecimal.valueOf(y));
        return point;
    }
}
