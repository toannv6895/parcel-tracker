package com.hotel.parceltracker.service;

import java.util.List;

public interface BaseService<T, ID> {
    T create(T dto);
    T findById(ID id);
    List<T> findAll();
    T update(ID id, T dto);
    void delete(ID id);
}