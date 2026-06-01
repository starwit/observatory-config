package de.starwit.rest.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.starwit.persistence.entity.PointEntity;
import de.starwit.rest.controller.PointController;
import de.starwit.service.impl.PointService;
import jakarta.persistence.EntityNotFoundException;

public class PointControllerIntegrationTest {

    @Mock
    private PointService pointService;

    @InjectMocks
    private PointController pointController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_ReturnsEntity() {
        Long id = 1L;
        PointEntity entity = new PointEntity();
        entity.setId(id);
        when(pointService.findById(id)).thenReturn(entity);

        PointEntity result = pointController.findById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(pointService, times(1)).findById(id);
    }

    @Test
    void testFindById_EntityNotFoundException() {
        Long id = 2L;
        when(pointService.findById(id)).thenThrow(new EntityNotFoundException("Not found"));

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            pointController.findById(id);
        });

        assertEquals("Not found", thrown.getMessage());
        verify(pointService, times(1)).findById(id);
    }

}
