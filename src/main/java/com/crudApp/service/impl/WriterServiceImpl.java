package com.crudApp.service.impl;

import com.crudApp.model.Writer;
import com.crudApp.repository.WriterRepository;
import com.crudApp.service.WriterService;

import java.util.List;

public class WriterServiceImpl implements WriterService {

    private final WriterRepository writerRepository;

    public WriterServiceImpl(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Override
    public Writer create(String firstName, String lastName) {
        Writer writer = new Writer(null, firstName, lastName, List.of());
        return writerRepository.save(writer);
    }

    @Override
    public Writer update(Long id, String firstName, String lastName) {
        Writer writer = writerRepository.findById(id);
        if (writer == null) {
            throw new RuntimeException("Writer not found with id: " + id);
        }
        writer.setFirstName(firstName);
        writer.setLastName(lastName);
        return writerRepository.update(writer);
    }

    @Override
    public void delete(Long id) {
        writerRepository.deleteById(id);
    }

    @Override
    public Writer getById(Long id) {
        return writerRepository.findById(id);
    }

    @Override
    public List<Writer> getAll() {
        return writerRepository.findAll();
    }
}
