package com.crudApp.service.impl;

import com.crudApp.model.Label;
import com.crudApp.repository.LabelRepository;
import com.crudApp.service.LabelService;

import java.util.List;

public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    public LabelServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    public Label create(String name) {
        return labelRepository.save(new Label(null, name));
    }

    @Override
    public Label update(Long id, String name) {
        Label existing = labelRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Label not found with id " + id);
        }
        existing.setName(name);
        return labelRepository.update(existing);
    }

    @Override
    public Label delete(Long id) {
        labelRepository.deleteById(id);
        return null;
    }

    @Override
    public Label getById(Long id) {
        return labelRepository.findById(id);
    }

    @Override
    public List<Label> getAll() {
        return labelRepository.findAll();
    }
}
