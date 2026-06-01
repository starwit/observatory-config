package de.starwit.rest.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.starwit.persistence.entity.ClassificationEntity;
import de.starwit.rest.controller.ClassificationController;
import de.starwit.service.impl.ClassificationService;
import jakarta.persistence.EntityNotFoundException;

public class ClassificationControllerIntegrationTest {

    @Mock
    private ClassificationService classificationService;

    @InjectMocks
    ClassificationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_ReturnsEntity() {
        Long id = 1L;
        ClassificationEntity entity = new ClassificationEntity();
        entity.setId(id);
        when(classificationService.findById(id)).thenReturn(entity);

        ClassificationEntity result = controller.findById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(classificationService, times(1)).findById(id);
    }

    @Test
    void testFindById_EntityNotFoundException() {
        Long id = 2L;
        when(classificationService.findById(id)).thenThrow(new EntityNotFoundException("Not found"));

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            controller.findById(id);
        });

        assertEquals("Not found", thrown.getMessage());
        verify(classificationService, times(1)).findById(id);
    }

}
