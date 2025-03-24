package com.hotel.parceltracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotel.parceltracker.entity.GuestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuestDto {
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    private GuestStatus status;

    @NotNull(message = "Check-in time is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutTime;
}
