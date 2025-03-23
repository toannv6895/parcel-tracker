package com.hotel.parceltracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.entity.Guest;
import com.hotel.parceltracker.entity.GuestStatus;
import com.hotel.parceltracker.repository.GuestRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "test")
class GuestControllerIT extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GuestRepository guestRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Guest guest;

    @BeforeEach
    void setup() {
        guestRepository.deleteAll();
        guest = new Guest();
        guest.setName("John Doe");
        guest.setCheckInTime(LocalDateTime.now());
        guest = guestRepository.save(guest);
    }

    @Test
    void testCreateGuest_Success() throws Exception {
        GuestDto newGuest = new GuestDto();
        newGuest.setName("Jane Doe");
        newGuest.setStatus(GuestStatus.CHECKED_IN);
        newGuest.setCheckInTime(LocalDateTime.now().minusDays(1));
        newGuest.setCheckInTime(LocalDateTime.now());

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGuest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void testGetGuestById_Success() throws Exception {
        mockMvc.perform(get("/api/guests/" + guest.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testUpdateGuest_Success() throws Exception {
        guest.setName("John Smith");

        mockMvc.perform(put("/api/guests/" + guest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Smith"));
    }

    @Test
    void testDeleteGuest_Success() throws Exception {
        mockMvc.perform(delete("/api/guests/" + guest.getId()))
                .andExpect(status().isNoContent());

        assertFalse(guestRepository.findById(guest.getId()).isPresent());
    }

    // Todo: Add more test cases
}

