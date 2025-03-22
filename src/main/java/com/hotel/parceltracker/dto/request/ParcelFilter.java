package com.hotel.parceltracker.dto.request;

import com.hotel.parceltracker.entity.ParcelStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParcelFilter {
    private Long guestId;
    private ParcelStatus status;
}