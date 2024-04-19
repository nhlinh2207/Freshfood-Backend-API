package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.mapper.MapperUtils;
import com.linh.freshfoodbackend.repository.BaseRepo;
import com.linh.freshfoodbackend.service.BaseService;
import javassist.NotFoundException;

import java.util.List;

public abstract class BaseServiceImpl<T> implements BaseService<T> {

    protected abstract BaseRepo<T> getBaseRepository();

    @Override
    public List<T> getAll() {
        return getBaseRepository().findAll();
    }

    @Override
    public T findById(Integer id) throws NotFoundException {
        return getBaseRepository().findById(id).orElseThrow(
                () -> new NotFoundException("Not found entity By id : "+id)
        );
    }

    @Override
    public void delete(Integer id) {
        getBaseRepository().deleteById(id);
    }

    @Override
    public T create(T t) {
        return getBaseRepository().saveAndFlush(t);
    }

    @Override
    public void update(Integer id, T t) throws NotFoundException{
        T old = findById(id);
        MapperUtils.map(t, old);
        getBaseRepository().saveAndFlush(old);
    }
}
