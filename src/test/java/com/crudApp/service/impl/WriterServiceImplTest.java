package com.crudApp.service.impl;

import com.crudApp.model.Writer;
import com.crudApp.repository.WriterRepository;
import com.crudApp.service.WriterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

public class WriterServiceImplTest {

    private WriterRepository writerRepository;
    private WriterService writerService;

    @BeforeEach
    void setUp() {
        writerRepository = mock(WriterRepository.class);
        writerService = new WriterServiceImpl(writerRepository);
    }

    @Test
    void testCreateWriter() {
        Writer toSave = new Writer(null, "Alex", "Pushkin", List.of());
        Writer saved = new Writer(1L, "Alex", "Pushkin", List.of());

        when(writerRepository.save(any(Writer.class))).thenReturn(saved);

        Writer result = writerService.create("Alex", "Pushkin");

        assertEquals("Alex", result.getFirstName());
        assertEquals(1L, result.getId());
        verify(writerRepository).save(any(Writer.class));
    }

    @Test
    void testUpdateWriter() {
        Writer existing = new Writer(1L, "Old", "Name", List.of());
        Writer updated = new Writer(1L, "New", "Name", List.of());

        when(writerRepository.findById(1L)).thenReturn(existing);
        when(writerRepository.update(existing)).thenReturn(updated);

        Writer result = writerService.update(1L, "New", "Name");

        assertEquals("New", result.getFirstName());
        verify(writerRepository).update(existing);
    }

    @Test
    void testDeleteWriter() {
        writerService.delete(1L);
        verify(writerRepository).deleteById(1L);
    }

    @Test
    void testGetById() {
        Writer writer = new Writer(1L, "A", "B", List.of());
        when(writerRepository.findById(1L)).thenReturn(writer);

        Writer result = writerService.getById(1L);
        assertEquals("A", result.getFirstName());
        verify(writerRepository).findById(1L);
    }

    @Test
    void testGetAll() {
        List<Writer> list = Arrays.asList(
                new Writer(1L, "A", "B", List.of()),
                new Writer(2L, "C", "D", List.of())
        );
        when(writerRepository.findAll()).thenReturn(list);

        List<Writer> result = writerService.getAll();
        assertEquals(2, result.size());
        verify(writerRepository).findAll();
    }

}
