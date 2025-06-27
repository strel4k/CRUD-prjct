package com.crudApp.service.impl;

import com.crudApp.model.Label;
import com.crudApp.repository.LabelRepository;
import com.crudApp.service.LabelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LabelServiceImplTest {
    private LabelRepository labelRepository;
    private LabelService labelService;

    @BeforeEach
    void setUp() {
        labelRepository = mock(LabelRepository.class);
        labelService = new LabelServiceImpl(labelRepository);
    }

    @Test
    void testUpdateLabel() {
        Label existing = new Label(1L, "Old");
        Label updated = new Label(1L, "New");

        when(labelRepository.findById(1L)).thenReturn(existing);
        when(labelRepository.update(existing)).thenReturn(updated);

        Label result = labelService.update(1L, "New");
        assertEquals("New", result.getName());
        verify(labelRepository).update(existing);
    }

    @Test
    void testDeleteLabel() {
        labelService.delete(1L);
        verify(labelRepository).deleteById(1L);
    }

    @Test
    void testGetById() {
        Label label = new Label(1L, "Test");
        when(labelRepository.findById(1L)).thenReturn(label);

        Label result = labelService.getById(1L);

        assertEquals("Test", result.getName());
        verify(labelRepository).findById(1L);
    }

    @Test
    void testGetAll() {
        List<Label> labels = Arrays.asList(new Label(1L, "A"), new Label(2L, "B"));
        when(labelRepository.findAll()).thenReturn(labels);

        List<Label> result = labelService.getAll();

        assertEquals(2, result.size());
        verify(labelRepository).findAll();
    }
}
