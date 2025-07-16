package com.crudApp.repository;

import com.crudApp.model.Label;

import java.util.List;

public interface LabelRepository extends GenericRepository<Label, Long> {
    List<Label> findAllLabelsByIdIn(List<Long> Ids);
}