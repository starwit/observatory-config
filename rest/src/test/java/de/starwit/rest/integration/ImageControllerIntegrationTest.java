package de.starwit.rest.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.starwit.persistence.entity.ImageEntity;
import de.starwit.rest.controller.ImageController;
import de.starwit.service.impl.ClassificationService;
import de.starwit.service.impl.ImageService;
import de.starwit.service.impl.ObservationAreaService;
import de.starwit.service.impl.PointService;
import de.starwit.service.impl.PolygonService;
import de.starwit.service.mapper.ImageMapper;
import jakarta.persistence.EntityNotFoundException;

public class ImageControllerIntegrationTest {

    @Mock
    private ClassificationService classificationService;

    @Mock
    private ObservationAreaService observationAreaService;

    @Mock
    private PolygonService polygonService;

    @Mock
    private PointService pointService;

    @Mock
    private ImageMapper mapper;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_ReturnsEntity() {
        Long id = 1L;
        ImageEntity entity = new ImageEntity();
        entity.setId(id);
        when(imageService.findById(id)).thenReturn(entity);

        ImageEntity result = controller.findById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(imageService, times(1)).findById(id);
    }

    @Test
    void testFindById_EntityNotFoundException() {
        Long id = 2L;
        when(imageService.findById(id)).thenThrow(new EntityNotFoundException("Not found"));

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            controller.findById(id);
        });

        assertEquals("Not found", thrown.getMessage());
        verify(imageService, times(1)).findById(id);
    }

}
