package com.crudApp.service;

import com.crudApp.model.Writer;

import java.util.List;

public interface WriterService {
    Writer create(String firstName, String lastName);
    Writer update(Long id, String firstName, String lastName);
    void delete(Long id);
    Writer getById(Long id);
    List<Writer> getAll();
}
