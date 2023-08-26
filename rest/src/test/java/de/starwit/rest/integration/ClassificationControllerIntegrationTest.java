package de.starwit.rest.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.rest.controller.ClassificationController;
import de.starwit.service.impl.ClassificationService;

/**
 * Tests for ClassificationController
 *
 * <pre>
 * @WebMvcTest also auto-configures MockMvc which offers a powerful way of
 * easy testing MVC controllers without starting a full HTTP server.
 * </pre>
 */
@WebMvcTest(controllers = ClassificationController.class)
public class ClassificationControllerIntegrationTest extends AbstractControllerIntegrationTest<ClassificationEntity> {

    @MockBean
    private ClassificationService classificationService;

    private JacksonTester<ClassificationEntity> jsonClassificationEntity;
    private static final String data = "testdata/classification/";
    private static final String restpath = "/api/classifications/";

    @Override
    public Class<ClassificationEntity> getEntityClass() {
        return ClassificationEntity.class;
    }

    @Override
    public String getRestPath() {
        return restpath;
    }

    // implement tests here
    @Test
    public void canRetrieveById() throws Exception {

        // ClassificationEntity entityToTest = readFromFile(data +
        // "classification.json");
        // when(appService.findById(0L)).thenReturn(entityToTest);

        // MockHttpServletResponse response = retrieveById(0L);

        // then
        // assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        // assertThat(response.getContentAsString())
        // .isEqualTo(jsonAppDto.write(dto).getJson());
    }

}
