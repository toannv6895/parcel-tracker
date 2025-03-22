package com.hotel.parceltracker.service;

import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.entity.Guest;
import com.hotel.parceltracker.entity.GuestStatus;
import com.hotel.parceltracker.exception.ResourceNotFoundException;
import com.hotel.parceltracker.mapper.GuestMapper;
import com.hotel.parceltracker.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceImplTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestMapper guestMapper;

    @InjectMocks
    private GuestServiceImpl guestService;

    private Guest guest;
    private GuestDto guestDto;

    @BeforeEach
    void setUp() {
        guest = new Guest();
        guest.setId(1L);
        guest.setName("John Doe");
        guest.setStatus(GuestStatus.CHECKED_IN);
        guest.setCheckInTime(LocalDateTime.now());

        guestDto = new GuestDto();
        guestDto.setId(1L);
        guestDto.setName("John Doe");
        guestDto.setStatus(GuestStatus.CHECKED_IN);
        guestDto.setCheckInTime(guest.getCheckInTime());
    }

    @Test
    void testCreateGuest_success() {
        // Arrange
        when(guestMapper.toEntity(any(GuestDto.class))).thenReturn(guest);
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);
        when(guestMapper.toDto(any(Guest.class))).thenReturn(guestDto);

        // Act
        GuestDto result = guestService.create(guestDto);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals(GuestStatus.CHECKED_IN, result.getStatus());
        verify(guestRepository, times(1)).save(any(Guest.class));
    }

    @Test
    void testFindById_success() {
        // Arrange
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(guestMapper.toDto(guest)).thenReturn(guestDto);

        // Act
        GuestDto result = guestService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(guestRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_notFound() {
        // Arrange
        when(guestRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> guestService.findById(1L));
        verify(guestRepository, times(1)).findById(1L);
    }

    @Test
    void testCheckOut_success() {
        // Arrange
        guest.setStatus(GuestStatus.CHECKED_IN);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);
        when(guestMapper.toDto(guest)).thenReturn(guestDto);

        // Act
        GuestDto result = guestService.checkOut(1L);

        // Assert
        assertNotNull(result);
        assertEquals(GuestStatus.CHECKED_OUT, guest.getStatus());
        assertNotNull(guest.getCheckOutTime());
        verify(guestRepository, times(1)).save(guest);
    }
}