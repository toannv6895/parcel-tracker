package com.hotel.parceltracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotel.parceltracker.entity.GuestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GuestDto {
    private Long id;
    private String name;
    private GuestStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutTime;
}
