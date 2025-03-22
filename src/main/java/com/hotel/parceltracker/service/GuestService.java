package com.hotel.parceltracker.service;

import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.dto.request.GuestFilter;

import java.util.List;

public interface GuestService extends BaseService<GuestDto, Long> {
    List<GuestDto> search(GuestFilter filter);
    GuestDto checkOut(Long id);
}