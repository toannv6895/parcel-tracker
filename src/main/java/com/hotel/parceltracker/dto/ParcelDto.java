package com.hotel.parceltracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotel.parceltracker.entity.ParcelStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParcelDto {
    private Long id;
    private Long guestId;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedDate;

    private ParcelStatus status;
}