package com.crudApp.controller;

import com.crudApp.model.Writer;
import com.crudApp.service.WriterService;

import java.util.List;

public class WriterController {

    private final WriterService writerService;

    public WriterController(WriterService writerService) {
        this.writerService = writerService;
    }

    public Writer create(String firstName, String lastName, List<Long> postIds) {
        return writerService.create(firstName, lastName);
    }

    public Writer update(Long id, String firstName, String lastName) {
        return writerService.update(id, firstName, lastName);
    }

    public void delete(Long id) {
        writerService.delete(id);
    }

    public Writer getById(Long id) {
        return writerService.getById(id);
    }

    public List<Writer> getAll() {
        return writerService.getAll();
    }
}
