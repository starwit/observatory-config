package de.starwit.rest.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.rest.controller.ImageController;
import de.starwit.service.impl.ClassificationService;
import de.starwit.service.impl.ImageService;
import de.starwit.service.impl.ObservationAreaService;
import de.starwit.service.impl.PointService;
import de.starwit.service.impl.PolygonService;
import de.starwit.service.mapper.ImageMapper;

/**
 * Tests for ImageController
 *
 * <pre>
 * @WebMvcTest also auto-configures MockMvc which offers a powerful way of
 * easy testing MVC controllers without starting a full HTTP server.
 * </pre>
 */
@WebMvcTest(controllers = ImageController.class)
public class ImageControllerIntegrationTest extends AbstractControllerIntegrationTest<ImageEntity> {

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private ClassificationService classificationService;

    @MockitoBean
    private ObservationAreaService observationAreaService;

    @MockitoBean
    private PolygonService polygonService;

    @MockitoBean
    private PointService pointService;

    @MockitoBean
    private ImageMapper mapper;

    private JacksonTester<ImageEntity> jsonImageEntity;
    private static final String data = "testdata/image/";
    private static final String restpath = "/api/images/";

    @Override
    public Class<ImageEntity> getEntityClass() {
        return ImageEntity.class;
    }

    @Override
    public String getRestPath() {
        return restpath;
    }

    // implement tests here
    @Test
    public void canRetrieveById() throws Exception {

        // ImageEntity entityToTest = readFromFile(data + "image.json");
        // when(appService.findById(0L)).thenReturn(entityToTest);

        // MockHttpServletResponse response = retrieveById(0L);

        // then
        // assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        // assertThat(response.getContentAsString())
        // .isEqualTo(jsonAppDto.write(dto).getJson());
    }

}
