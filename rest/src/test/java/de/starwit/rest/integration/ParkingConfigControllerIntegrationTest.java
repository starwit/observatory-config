package de.starwit.rest.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import de.starwit.persistence.entity.ParkingConfigEntity;
import de.starwit.rest.controller.ParkingConfigController;
import de.starwit.service.impl.ParkingConfigService;

/**
 * Tests for ParkingConfigController
 *
 * <pre>
 * @WebMvcTest also auto-configures MockMvc which offers a powerful way of
 * easy testing MVC controllers without starting a full HTTP server.
 * </pre>
 */
@WebMvcTest(controllers = ParkingConfigController.class)
public class ParkingConfigControllerIntegrationTest extends AbstractControllerIntegrationTest<ParkingConfigEntity> {

    @MockBean
    private ParkingConfigService parkingconfigService;

    private JacksonTester<ParkingConfigEntity> jsonParkingConfigEntity;
    private static final String data = "testdata/parkingconfig/";
    private static final String restpath = "/api/parkingconfigs/";

    @Override
    public Class<ParkingConfigEntity> getEntityClass() {
        return ParkingConfigEntity.class;
    }

    @Override
    public String getRestPath() {
        return restpath;
    }

    //implement tests here
    @Test
    public void canRetrieveById() throws Exception {

//        ParkingConfigEntity entityToTest = readFromFile(data + "parkingconfig.json");
//        when(appService.findById(0L)).thenReturn(entityToTest);

//        MockHttpServletResponse response = retrieveById(0L);

        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString())
//                .isEqualTo(jsonAppDto.write(dto).getJson());
    }

}
