package com.hotel.parceltracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.dto.request.GuestFilter;
import com.hotel.parceltracker.exception.BadRequestException;
import com.hotel.parceltracker.exception.GlobalExceptionHandler;
import com.hotel.parceltracker.exception.ResourceNotFoundException;
import com.hotel.parceltracker.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GuestControllerTest {

    private static final Long EXISTING_GUEST_ID = 1L;
    private static final Long NON_EXISTING_GUEST_ID = 999L;
    private static final String API_GUESTS = "/api/guests";
    private static final String API_GUESTS_EXISTING_GUEST_ID = String.format("%s/%d", API_GUESTS, EXISTING_GUEST_ID);
    private static final String API_GUESTS_NON_EXISTING_GUEST_ID = String.format("%s/%d", API_GUESTS, NON_EXISTING_GUEST_ID);
    private static final String EXISTING_GUEST_NAME = "John Doe";
    private static final String NOT_FOUND_MESSAGE = "Guest not found with id: ";
    private static final String ALREADY_CHECKED_OUT_MESSAGE = "Guest is already checked out";


    private MockMvc mockMvc;

    @Mock
    private GuestService guestService;

    @InjectMocks
    private GuestController guestController;

    private GuestDto guestDto;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(guestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        guestDto = new GuestDto();
        guestDto.setId(EXISTING_GUEST_ID);
        guestDto.setName(EXISTING_GUEST_NAME);
        guestDto.setCheckInTime(LocalDateTime.now());
    }

    @Test
    void testCreateGuest_Success() throws Exception {
        when(guestService.create(any(GuestDto.class))).thenReturn(guestDto);

        mockMvc.perform(post(API_GUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(EXISTING_GUEST_NAME));

        verify(guestService, times(1)).create(any(GuestDto.class));
    }

    @Test
    void testGetGuestById_Success() throws Exception {
        when(guestService.findById(EXISTING_GUEST_ID)).thenReturn(guestDto);

        mockMvc.perform(get(API_GUESTS_EXISTING_GUEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(guestService, times(1)).findById(EXISTING_GUEST_ID);
    }

    @Test
    void testGetGuestById_NotFound() throws Exception {
        when(guestService.findById(NON_EXISTING_GUEST_ID))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE + NON_EXISTING_GUEST_ID));

        mockMvc.perform(get(API_GUESTS_NON_EXISTING_GUEST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE + NON_EXISTING_GUEST_ID));
    }

    @Test
    void testUpdateGuest_Success() throws Exception {
        when(guestService.update(anyLong(), any(GuestDto.class))).thenReturn(guestDto);

        mockMvc.perform(put(API_GUESTS_EXISTING_GUEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(EXISTING_GUEST_NAME));

        verify(guestService, times(1)).update(anyLong(), any(GuestDto.class));
    }

    @Test
    void testDeleteGuest_Success() throws Exception {
        doNothing().when(guestService).delete(EXISTING_GUEST_ID);

        mockMvc.perform(delete(API_GUESTS_EXISTING_GUEST_ID))
                .andExpect(status().isNoContent());

        verify(guestService, times(1)).delete(EXISTING_GUEST_ID);
    }

    @Test
    void testSearchGuests_Success() throws Exception {
        when(guestService.search(any(GuestFilter.class))).thenReturn(List.of(guestDto));

        mockMvc.perform(get(API_GUESTS + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(EXISTING_GUEST_NAME));
    }

    @Test
    void testCheckOutGuest_Success() throws Exception {
        when(guestService.checkOut(EXISTING_GUEST_ID)).thenReturn(guestDto);

        mockMvc.perform(put(API_GUESTS_EXISTING_GUEST_ID + "/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateGuest_Fail_InvalidData() throws Exception {
        GuestDto invalidGuest = new GuestDto();

        mockMvc.perform(post(API_GUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGuest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateGuest_NotFound() throws Exception {
        when(guestService.update(eq(NON_EXISTING_GUEST_ID), any(GuestDto.class)))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE + NON_EXISTING_GUEST_ID));

        mockMvc.perform(put(API_GUESTS_NON_EXISTING_GUEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE + NON_EXISTING_GUEST_ID));
    }

    @Test
    void testDeleteGuest_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE + NON_EXISTING_GUEST_ID))
                .when(guestService).delete(NON_EXISTING_GUEST_ID);

        mockMvc.perform(delete(API_GUESTS_NON_EXISTING_GUEST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE + NON_EXISTING_GUEST_ID));
    }

    @Test
    void testSearchGuests_NoResults() throws Exception {
        when(guestService.search(any(GuestFilter.class))).thenReturn(List.of());

        mockMvc.perform(get(API_GUESTS + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testCheckOutGuest_NotFound() throws Exception {
        when(guestService.checkOut(NON_EXISTING_GUEST_ID))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE + NON_EXISTING_GUEST_ID));

        mockMvc.perform(put(API_GUESTS_NON_EXISTING_GUEST_ID + "/checkout"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE + NON_EXISTING_GUEST_ID));
    }

    @Test
    void testCheckOutGuest_AlreadyCheckedOut() throws Exception {
        when(guestService.checkOut(EXISTING_GUEST_ID))
                .thenThrow(new BadRequestException(ALREADY_CHECKED_OUT_MESSAGE));

        mockMvc.perform(put(API_GUESTS_EXISTING_GUEST_ID + "/checkout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ALREADY_CHECKED_OUT_MESSAGE));
    }
}
