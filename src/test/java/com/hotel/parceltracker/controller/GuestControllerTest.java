package com.hotel.parceltracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.entity.GuestStatus;
import com.hotel.parceltracker.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuestController.class)
class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuestService guestService;

    @Autowired
    private ObjectMapper objectMapper;

    private GuestDto guestDto;

    @BeforeEach
    void setUp() {
        guestDto = new GuestDto();
        guestDto.setId(1L);
        guestDto.setName("John Doe");
        guestDto.setStatus(GuestStatus.CHECKED_IN);
        guestDto.setCheckInTime(LocalDateTime.now());
    }

    @Test
    void testCreateGuest_success() throws Exception {
        // Arrange
        when(guestService.create(any(GuestDto.class))).thenReturn(guestDto);

        // Act & Assert
        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(guestService, times(1)).create(any(GuestDto.class));
    }

    @Test
    void testGetGuestById_success() throws Exception {
        // Arrange
        when(guestService.findById(1L)).thenReturn(guestDto);

        // Act & Assert
        mockMvc.perform(get("/api/guests/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(guestService, times(1)).findById(1L);
    }

    @Test
    void testCheckOutGuest_success() throws Exception {
        // Arrange
        guestDto.setStatus(GuestStatus.CHECKED_OUT);
        when(guestService.checkOut(1L)).thenReturn(guestDto);

        // Act & Assert
        mockMvc.perform(put("/api/guests/1/checkout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_OUT"));

        verify(guestService, times(1)).checkOut(1L);
    }
}