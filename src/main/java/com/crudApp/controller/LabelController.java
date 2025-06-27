package com.crudApp.controller;

import com.crudApp.model.Label;
import com.crudApp.service.LabelService;

import java.util.List;

public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    public Label create(String name) {
        return labelService.create(name);
    }

    public Label update(Long id, String name) {
        return labelService.update(id, name);
    }

    public Label delete(Long id) {
        return labelService.delete(id);
    }

    public Label getById(Long id) {
        return labelService.getById(id);
    }

    public List<Label> getAll() {
        return labelService.getAll();
    }
}
