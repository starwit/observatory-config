package de.starwit.rest.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.rest.controller.PolygonController;
import de.starwit.service.impl.PolygonService;

/**
 * Tests for PolygonController
 *
 * <pre>
 * @WebMvcTest also auto-configures MockMvc which offers a powerful way of
 * easy testing MVC controllers without starting a full HTTP server.
 * </pre>
 */
@WebMvcTest(controllers = PolygonController.class)
public class PolygonControllerIntegrationTest extends AbstractControllerIntegrationTest<PolygonEntity> {

    @MockBean
    private PolygonService polygonService;

    private JacksonTester<PolygonEntity> jsonPolygonEntity;
    private static final String data = "testdata/polygon/";
    private static final String restpath = "/api/polygons/";

    @Override
    public Class<PolygonEntity> getEntityClass() {
        return PolygonEntity.class;
    }

    @Override
    public String getRestPath() {
        return restpath;
    }

    // implement tests here
    @Test
    public void canRetrieveById() throws Exception {

        // PolygonEntity entityToTest = readFromFile(data + "polygon.json");
        // when(appService.findById(0L)).thenReturn(entityToTest);

        // MockHttpServletResponse response = retrieveById(0L);

        // then
        // assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        // assertThat(response.getContentAsString())
        // .isEqualTo(jsonAppDto.write(dto).getJson());
    }

}
