package com.hotel.parceltracker.mapper;

import com.hotel.parceltracker.dto.ParcelDto;
import com.hotel.parceltracker.entity.Parcel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParcelMapper extends EntityMapper<ParcelDto, Parcel> {
    @Override
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "receivedDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Parcel toEntity(ParcelDto dto);

    @Override
    @Mapping(source = "guest.id", target = "guestId")
    ParcelDto toDto(Parcel entity);
}