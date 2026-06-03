package de.starwit.rest.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.starwit.persistence.entity.ObservationAreaEntity;
import de.starwit.rest.controller.ObservationAreaController;
import de.starwit.service.dto.ObservationAreaDto;
import de.starwit.service.impl.ClassificationService;
import de.starwit.service.impl.ObservationAreaService;
import de.starwit.service.impl.PointService;
import de.starwit.service.impl.PolygonService;
import jakarta.persistence.EntityNotFoundException;

public class ObservationAreaControllerIntegrationTest {

    @Mock
    private ObservationAreaService service;

    @Mock
    private ClassificationService classificationService;

    @Mock
    private PolygonService polygonService;

    @Mock
    private PointService pointService;

    @InjectMocks
    private ObservationAreaController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_ReturnsEntity() {
        Long id = 1L;
        ObservationAreaEntity entity = new ObservationAreaEntity();
        entity.setId(id);
        when(service.findById(id)).thenReturn(entity);

        ObservationAreaDto result = controller.findById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(service, times(1)).findById(id);
    }

    @Test
    void testFindById_EntityNotFoundException() {
        Long id = 2L;
        when(service.findById(id)).thenThrow(new EntityNotFoundException("Not found"));

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            controller.findById(id);
        });

        assertEquals("Not found", thrown.getMessage());
        verify(service, times(1)).findById(id);
    }

}
