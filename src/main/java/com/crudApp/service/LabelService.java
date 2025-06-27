package com.crudApp.service;

import com.crudApp.model.Label;

import java.util.List;

public interface LabelService {
    Label create(String name);
    Label update(Long id, String name);
    Label delete(Long id);
    Label getById(Long id);
    List<Label> getAll();
}
