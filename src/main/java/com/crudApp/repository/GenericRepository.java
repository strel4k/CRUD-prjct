package com.crudApp.repository;

import java.util.List;

public interface GenericRepository<T, ID> {
    T save(T t);
    T update(T t);
    T findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
