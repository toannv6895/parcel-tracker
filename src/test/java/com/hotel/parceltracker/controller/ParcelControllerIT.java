package com.hotel.parceltracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.parceltracker.dto.ParcelDto;
import com.hotel.parceltracker.entity.Guest;
import com.hotel.parceltracker.entity.GuestStatus;
import com.hotel.parceltracker.entity.Parcel;
import com.hotel.parceltracker.entity.ParcelStatus;
import com.hotel.parceltracker.repository.GuestRepository;
import com.hotel.parceltracker.repository.ParcelRepository;
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
import static com.hotel.parceltracker.constants.TestConstants.*;

@WithMockUser(username = "test")
class ParcelControllerIT extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private GuestRepository guestRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Parcel parcel;
    private ParcelDto parcelDto;
    private Guest guest;

    @BeforeEach
    void setup() {
        parcelRepository.deleteAll();
        guestRepository.deleteAll();

        guest = new Guest();
        guest.setName("John Doe");
        guest.setStatus(GuestStatus.CHECKED_IN);
        guest.setCheckInTime(LocalDateTime.now().minusDays(1));
        guest = guestRepository.saveAndFlush(guest);

        parcel = new Parcel();
        parcel.setGuest(guest);
        parcel.setDescription(PACKAGE_DESCRIPTION);
        parcel.setStatus(ParcelStatus.PENDING);
        parcel = parcelRepository.saveAndFlush(parcel);

        parcelDto = new ParcelDto();
        parcelDto.setGuestId(guest.getId());
        parcelDto.setDescription(PACKAGE_DESCRIPTION);
        parcelDto.setStatus(ParcelStatus.PENDING);
    }

    @Test
    void testCreateParcel_Success() throws Exception {
        ParcelDto newParcelDto = new ParcelDto();
        newParcelDto.setGuestId(guest.getId());
        newParcelDto.setDescription("New Package");
        newParcelDto.setStatus(ParcelStatus.PENDING);

        mockMvc.perform(post("/api/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newParcelDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("New Package"));
    }

    @Test
    void testGetParcelById_Success() throws Exception {
        mockMvc.perform(get("/api/parcels/" + parcel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(parcel.getId()));
    }

    @Test
    void testUpdateParcel_Success() throws Exception {
        parcelDto.setId(parcel.getId());
        parcelDto.setDescription(UPDATED_DESCRIPTION);

        mockMvc.perform(put("/api/parcels/" + parcelDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parcelDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(UPDATED_DESCRIPTION));
    }

    @Test
    void testDeleteParcel_Success() throws Exception {
        mockMvc.perform(delete("/api/parcels/" + parcel.getId()))
                .andExpect(status().isNoContent());

        assertFalse(parcelRepository.findById(parcel.getId()).isPresent());
    }

    // Todo: Add more test cases
}

