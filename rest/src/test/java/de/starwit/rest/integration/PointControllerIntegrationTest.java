package de.starwit.rest.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import de.starwit.persistence.entity.PointEntity;
import de.starwit.rest.controller.PointController;
import de.starwit.service.impl.PointService;

/**
 * Tests for PointController
 *
 * <pre>
 * @WebMvcTest also auto-configures MockMvc which offers a powerful way of
 * easy testing MVC controllers without starting a full HTTP server.
 * </pre>
 */
@WebMvcTest(controllers = PointController.class)
public class PointControllerIntegrationTest extends AbstractControllerIntegrationTest<PointEntity> {

    @MockBean
    private PointService pointService;

    private JacksonTester<PointEntity> jsonPointEntity;
    private static final String data = "testdata/point/";
    private static final String restpath = "/api/points/";

    @Override
    public Class<PointEntity> getEntityClass() {
        return PointEntity.class;
    }

    @Override
    public String getRestPath() {
        return restpath;
    }

    // implement tests here
    @Test
    public void canRetrieveById() throws Exception {

        // PointEntity entityToTest = readFromFile(data + "point.json");
        // when(appService.findById(0L)).thenReturn(entityToTest);

        // MockHttpServletResponse response = retrieveById(0L);

        // then
        // assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        // assertThat(response.getContentAsString())
        // .isEqualTo(jsonAppDto.write(dto).getJson());
    }

}
