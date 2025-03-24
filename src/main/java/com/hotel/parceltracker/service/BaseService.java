package com.hotel.parceltracker.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T, ID> {
    T create(T dto);
    T findById(ID id);
    Page<T> findAll(Pageable pageable);
    T update(ID id, T dto);
    void delete(ID id);
}