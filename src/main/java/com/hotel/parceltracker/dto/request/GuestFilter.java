package com.hotel.parceltracker.dto.request;

import com.hotel.parceltracker.entity.GuestStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestFilter {
    private String name;
    private GuestStatus status;
}