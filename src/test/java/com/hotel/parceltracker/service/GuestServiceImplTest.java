package com.hotel.parceltracker.service;

import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.dto.request.GuestFilter;
import com.hotel.parceltracker.entity.Guest;
import com.hotel.parceltracker.entity.GuestStatus;
import com.hotel.parceltracker.exception.BadRequestException;
import com.hotel.parceltracker.exception.ResourceNotFoundException;
import com.hotel.parceltracker.mapper.GuestMapper;
import com.hotel.parceltracker.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceImplTest {

    private static final Long EXISTING_GUEST_ID = 1L;
    private static final Long NON_EXISTING_GUEST_ID = 999L;
    private static final String EXISTING_GUEST_NAME = "John Doe";

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
        guest.setId(EXISTING_GUEST_ID);
        guest.setName(EXISTING_GUEST_NAME);
        guest.setStatus(GuestStatus.CHECKED_IN);
        guest.setCheckInTime(LocalDateTime.now().minusDays(1));

        guestDto = new GuestDto();
        guestDto.setId(EXISTING_GUEST_ID);
        guestDto.setName(EXISTING_GUEST_NAME);
        guestDto.setStatus(GuestStatus.CHECKED_IN);
        guestDto.setCheckInTime(guest.getCheckInTime());
    }

    @Test
    void testCreateGuest_Success() {
        when(guestMapper.toEntity(any(GuestDto.class))).thenReturn(guest);
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);
        when(guestMapper.toDto(any(Guest.class))).thenReturn(guestDto);

        GuestDto result = guestService.create(guestDto);

        assertNotNull(result);
        assertEquals(EXISTING_GUEST_ID, result.getId());
        assertEquals(EXISTING_GUEST_NAME, result.getName());
        assertEquals(GuestStatus.CHECKED_IN, result.getStatus());
        verify(guestRepository, times(1)).save(any(Guest.class));
    }

    @Test
    void testFindById_Success() {
        when(guestRepository.findById(EXISTING_GUEST_ID)).thenReturn(Optional.of(guest));
        when(guestMapper.toDto(any(Guest.class))).thenReturn(guestDto);

        GuestDto result = guestService.findById(EXISTING_GUEST_ID);

        assertNotNull(result);
        assertEquals(EXISTING_GUEST_ID, result.getId());
        assertEquals(EXISTING_GUEST_NAME, result.getName());
        verify(guestRepository, times(1)).findById(EXISTING_GUEST_ID);
    }

    @Test
    void testFindById_NotFound() {
        when(guestRepository.findById(NON_EXISTING_GUEST_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> guestService.findById(NON_EXISTING_GUEST_ID));
        verify(guestRepository, times(1)).findById(NON_EXISTING_GUEST_ID);
    }

    @Test
    void testUpdateGuest_Success() {
        GuestDto updatedDto = new GuestDto();
        updatedDto.setId(EXISTING_GUEST_ID);
        updatedDto.setName("Jane Doe");

        when(guestRepository.findById(EXISTING_GUEST_ID)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);
        when(guestMapper.toDto(any(Guest.class))).thenReturn(updatedDto);

        GuestDto result = guestService.update(EXISTING_GUEST_ID, updatedDto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        verify(guestRepository, times(1)).save(any(Guest.class));
    }

    @Test
    void testUpdateGuest_NotFound() {
        when(guestRepository.findById(NON_EXISTING_GUEST_ID)).thenReturn(Optional.empty());

        GuestDto dto = new GuestDto();
        dto.setName("Updated Name");

        assertThrows(ResourceNotFoundException.class, () -> guestService.update(NON_EXISTING_GUEST_ID, dto));

        verify(guestRepository, times(1)).findById(NON_EXISTING_GUEST_ID);
    }


    @Test
    void testDeleteGuest_Success() {
        when(guestRepository.findById(EXISTING_GUEST_ID)).thenReturn(Optional.of(guest));
        doNothing().when(guestRepository).delete(any(Guest.class));

        guestService.delete(EXISTING_GUEST_ID);

        verify(guestRepository, times(1)).delete(any(Guest.class));
    }

    @Test
    void testDeleteGuest_NotFound() {
        when(guestRepository.findById(NON_EXISTING_GUEST_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> guestService.delete(NON_EXISTING_GUEST_ID));

        verify(guestRepository, times(1)).findById(NON_EXISTING_GUEST_ID);
    }

    @Test
    void testCheckOutGuest_Success() {
        when(guestRepository.findById(EXISTING_GUEST_ID)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> {
            Guest updatedGuest = invocation.getArgument(0);
            updatedGuest.setStatus(GuestStatus.CHECKED_OUT);
            updatedGuest.setCheckOutTime(LocalDateTime.now());
            return updatedGuest;
        });
        when(guestMapper.toDto(any(Guest.class))).thenAnswer(invocation -> {
            Guest updatedGuest = invocation.getArgument(0);
            return new GuestDto(
                    updatedGuest.getId(),
                    updatedGuest.getName(),
                    updatedGuest.getStatus(),
                    updatedGuest.getCheckInTime(),
                    updatedGuest.getCheckOutTime()
            );
        });

        GuestDto result = guestService.checkOut(EXISTING_GUEST_ID);

        assertEquals(GuestStatus.CHECKED_OUT, result.getStatus());
        assertNotNull(result.getCheckOutTime());
        assertTrue(result.getCheckOutTime().isAfter(result.getCheckInTime()));

        verify(guestRepository, times(1)).findById(EXISTING_GUEST_ID);
        verify(guestRepository, times(1)).save(any(Guest.class));
    }

    @Test
    void testCheckOut_alreadyCheckedOut() {
        guest.setStatus(GuestStatus.CHECKED_OUT);

        when(guestRepository.findById(EXISTING_GUEST_ID)).thenReturn(Optional.of(guest));

        assertThrows(BadRequestException.class, () -> guestService.checkOut(EXISTING_GUEST_ID));

        verify(guestRepository, times(1)).findById(EXISTING_GUEST_ID);
    }

    @Test
    void testCheckOut_guestNotFound() {
        when(guestRepository.findById(NON_EXISTING_GUEST_ID)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> guestService.checkOut(NON_EXISTING_GUEST_ID));
        verify(guestRepository, times(1)).findById(NON_EXISTING_GUEST_ID);
    }

    @Test
    void testSearch_success() {
        GuestFilter filter = new GuestFilter();
        filter.setName("John");

        List<Guest> guestList = List.of(guest);

        when(guestRepository.findAll(any(Specification.class))).thenReturn(guestList);
        when(guestMapper.toDto(guest)).thenReturn(guestDto);

        List<GuestDto> result = guestService.search(filter);

        assertEquals(1, result.size());
        assertEquals(EXISTING_GUEST_NAME, result.get(0).getName());

        verify(guestRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void testSearch_emptyFilter() {
        GuestFilter emptyFilter = new GuestFilter();

        when(guestRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<GuestDto> result = guestService.search(emptyFilter);

        assertTrue(result.isEmpty());
        verify(guestRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void testSearch_withMultipleCriteria() {
        GuestFilter filter = new GuestFilter();
        filter.setName("John");
        filter.setStatus(GuestStatus.CHECKED_IN);

        List<Guest> guestList = List.of(guest);
        when(guestRepository.findAll(any(Specification.class))).thenReturn(guestList);
        when(guestMapper.toDto(guest)).thenReturn(guestDto);

        List<GuestDto> result = guestService.search(filter);

        assertEquals(1, result.size());
        assertEquals(EXISTING_GUEST_NAME, result.get(0).getName());
        assertEquals(GuestStatus.CHECKED_IN, result.get(0).getStatus());

        verify(guestRepository, times(1)).findAll(any(Specification.class));
    }
}