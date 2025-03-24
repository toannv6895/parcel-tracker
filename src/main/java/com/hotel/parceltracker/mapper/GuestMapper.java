package com.hotel.parceltracker.mapper;

import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.entity.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GuestMapper extends EntityMapper<GuestDto, Guest> {
    @Override
    @Mapping(target = "checkInTime", ignore = true)
    @Mapping(target = "checkOutTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    Guest toEntity(GuestDto dto);

    @Override
    GuestDto toDto(Guest entity);
}