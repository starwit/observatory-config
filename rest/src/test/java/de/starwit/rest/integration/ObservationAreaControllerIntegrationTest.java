package de.starwit.rest.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.rest.controller.ObservationAreaController;
import de.starwit.service.impl.ClassificationService;
import de.starwit.service.impl.ObservationAreaService;
import de.starwit.service.impl.PointService;
import de.starwit.service.impl.PolygonService;

/**
 * Tests for ObservationAreaController
 *
 * <pre>
 * @WebMvcTest also auto-configures MockMvc which offers a powerful way of
 * easy testing MVC controllers without starting a full HTTP server.
 * </pre>
 */
@WebMvcTest(controllers = ObservationAreaController.class)
public class ObservationAreaControllerIntegrationTest extends AbstractControllerIntegrationTest<ObservationAreaEntity> {

    @MockBean
    private ObservationAreaService observationareaService;

    @MockBean
    private ClassificationService classificationService;

    @MockBean
    private PolygonService polygonService;

    @MockBean
    private PointService pointService;

    private JacksonTester<ObservationAreaEntity> jsonObservationAreaEntity;
    private static final String data = "testdata/observationarea/";
    private static final String restpath = "/api/observationareas/";

    @Override
    public Class<ObservationAreaEntity> getEntityClass() {
        return ObservationAreaEntity.class;
    }

    @Override
    public String getRestPath() {
        return restpath;
    }

    // implement tests here
    @Test
    public void canRetrieveById() throws Exception {

        // ObservationAreaEntity entityToTest = readFromFile(data + "observationarea.json");
        // when(appService.findById(0L)).thenReturn(entityToTest);

        // MockHttpServletResponse response = retrieveById(0L);

        // then
        // assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        // assertThat(response.getContentAsString())
        // .isEqualTo(jsonAppDto.write(dto).getJson());
    }

}
