package com.hotel.parceltracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.parceltracker.dto.ParcelDto;
import com.hotel.parceltracker.dto.request.ParcelFilter;
import com.hotel.parceltracker.entity.ParcelStatus;
import com.hotel.parceltracker.exception.BadRequestException;
import com.hotel.parceltracker.exception.GlobalExceptionHandler;
import com.hotel.parceltracker.exception.ResourceNotFoundException;
import com.hotel.parceltracker.service.ParcelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParcelControllerTest {

    private static final Long EXISTING_PARCEL_ID = 1L;
    private static final Long NON_EXISTENT_PARCEL_ID = 999L;
    private static final String API_BASE_PATH = "/api/parcels";
    private static final String ERROR_MESSAGE_NOT_FOUND = "Parcel not found with id: ";
    private static final String ERROR_MESSAGE_ALREADY_PICKED_UP = "Parcel is already picked up";

    private MockMvc mockMvc;

    @Mock
    private ParcelService parcelService;

    @InjectMocks
    private ParcelController parcelController;

    private ParcelDto parcelDto;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parcelController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        parcelDto = new ParcelDto();
        parcelDto.setId(EXISTING_PARCEL_ID);
        parcelDto.setStatus(ParcelStatus.PENDING);
    }

    @Test
    void testCreateParcel_Success() throws Exception {
        when(parcelService.create(any(ParcelDto.class))).thenReturn(parcelDto);

        mockMvc.perform(post(API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parcelDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(EXISTING_PARCEL_ID));

        verify(parcelService, times(1)).create(any(ParcelDto.class));
    }

    @Test
    void testGetParcelById_Success() throws Exception {
        when(parcelService.findById(EXISTING_PARCEL_ID)).thenReturn(parcelDto);

        mockMvc.perform(get(API_BASE_PATH + "/" + EXISTING_PARCEL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXISTING_PARCEL_ID));

        verify(parcelService, times(1)).findById(EXISTING_PARCEL_ID);
    }

    @Test
    void testGetParcelById_NotFound() throws Exception {
        when(parcelService.findById(NON_EXISTENT_PARCEL_ID))
                .thenThrow(new ResourceNotFoundException(ERROR_MESSAGE_NOT_FOUND + NON_EXISTENT_PARCEL_ID));

        mockMvc.perform(get(API_BASE_PATH + "/" + NON_EXISTENT_PARCEL_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ERROR_MESSAGE_NOT_FOUND + NON_EXISTENT_PARCEL_ID));
    }

    @Test
    void testUpdateParcel_Success() throws Exception {
        when(parcelService.update(anyLong(), any(ParcelDto.class))).thenReturn(parcelDto);

        mockMvc.perform(put(API_BASE_PATH + "/" + EXISTING_PARCEL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parcelDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXISTING_PARCEL_ID));
    }

    @Test
    void testDeleteParcel_Success() throws Exception {
        doNothing().when(parcelService).delete(EXISTING_PARCEL_ID);

        mockMvc.perform(delete(API_BASE_PATH + "/" + EXISTING_PARCEL_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteParcel_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException(ERROR_MESSAGE_NOT_FOUND + NON_EXISTENT_PARCEL_ID))
                .when(parcelService).delete(NON_EXISTENT_PARCEL_ID);

        mockMvc.perform(delete(API_BASE_PATH + "/" + NON_EXISTENT_PARCEL_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ERROR_MESSAGE_NOT_FOUND + NON_EXISTENT_PARCEL_ID));
    }

    @Test
    void testSearchParcels_Success() throws Exception {
        when(parcelService.search(any(ParcelFilter.class))).thenReturn(List.of(parcelDto));

        mockMvc.perform(get(API_BASE_PATH + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EXISTING_PARCEL_ID));
    }

    @Test
    void testSearchParcels_NoResults() throws Exception {
        when(parcelService.search(any(ParcelFilter.class))).thenReturn(List.of());

        mockMvc.perform(get(API_BASE_PATH + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testMarkParcelAsPickedUp_Success() throws Exception {
        parcelDto.setStatus(ParcelStatus.PICKED_UP);
        when(parcelService.markAsPickedUp(EXISTING_PARCEL_ID)).thenReturn(parcelDto);

        mockMvc.perform(put(API_BASE_PATH + "/" + EXISTING_PARCEL_ID + "/pickup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ParcelStatus.PICKED_UP.toString()));
    }

    @Test
    void testMarkParcelAsPickedUp_NotFound() throws Exception {
        when(parcelService.markAsPickedUp(NON_EXISTENT_PARCEL_ID))
                .thenThrow(new ResourceNotFoundException(ERROR_MESSAGE_NOT_FOUND + NON_EXISTENT_PARCEL_ID));

        mockMvc.perform(put(API_BASE_PATH + "/" + NON_EXISTENT_PARCEL_ID + "/pickup"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ERROR_MESSAGE_NOT_FOUND + NON_EXISTENT_PARCEL_ID));
    }

    @Test
    void testMarkParcelAsPickedUp_AlreadyPickedUp() throws Exception {
        when(parcelService.markAsPickedUp(EXISTING_PARCEL_ID))
                .thenThrow(new BadRequestException(ERROR_MESSAGE_ALREADY_PICKED_UP));

        mockMvc.perform(put(API_BASE_PATH + "/" + EXISTING_PARCEL_ID + "/pickup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ERROR_MESSAGE_ALREADY_PICKED_UP));
    }
}


