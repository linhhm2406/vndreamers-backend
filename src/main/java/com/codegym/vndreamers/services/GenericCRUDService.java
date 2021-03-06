package com.codegym.vndreamers.services;

import com.codegym.vndreamers.exceptions.EntityExistException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface GenericCRUDService<T> {
    List<T> findAll();

    List<T> findAll(Sort sort);

    Page<T> findAll(Pageable pageable);

    T findById(int id);

    T save(T model) throws SQLIntegrityConstraintViolationException, EntityExistException;

    T update(T model);

    boolean delete(int id);

}
