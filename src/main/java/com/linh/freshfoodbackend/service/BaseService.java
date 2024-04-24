package com.linh.freshfoodbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import javassist.NotFoundException;

import java.util.List;

public interface BaseService<T>{

    List<T> getAll();
    T findById(Integer id) throws NotFoundException;
    void delete(Integer id);
    T create(T t);
    void update(Integer id, T t) throws NotFoundException, JsonProcessingException, IllegalAccessException;
}
