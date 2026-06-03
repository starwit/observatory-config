package de.starwit.rest.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.starwit.persistence.entity.PolygonEntity;
import de.starwit.rest.controller.PolygonController;
import de.starwit.service.impl.PolygonService;
import jakarta.persistence.EntityNotFoundException;

public class PolygonControllerIntegrationTest {

    @Mock
    private PolygonService polygonService;

    @InjectMocks
    private PolygonController polygonController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_ReturnsEntity() {
        Long id = 1L;
        PolygonEntity entity = new PolygonEntity();
        entity.setId(id);
        when(polygonService.findById(id)).thenReturn(entity);

        PolygonEntity result = polygonController.findById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(polygonService, times(1)).findById(id);
    }

    @Test
    void testFindById_EntityNotFoundException() {
        Long id = 2L;
        when(polygonService.findById(id)).thenThrow(new EntityNotFoundException("Not found"));

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            polygonController.findById(id);
        });

        assertEquals("Not found", thrown.getMessage());
        verify(polygonService, times(1)).findById(id);
    }
}
