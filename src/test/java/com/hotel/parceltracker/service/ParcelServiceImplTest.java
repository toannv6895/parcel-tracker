package com.hotel.parceltracker.service;

import com.hotel.parceltracker.dto.ParcelDto;
import com.hotel.parceltracker.dto.request.ParcelFilter;
import com.hotel.parceltracker.entity.Guest;
import com.hotel.parceltracker.entity.Parcel;
import com.hotel.parceltracker.entity.ParcelStatus;
import com.hotel.parceltracker.entity.GuestStatus;
import com.hotel.parceltracker.exception.BadRequestException;
import com.hotel.parceltracker.exception.ResourceNotFoundException;
import com.hotel.parceltracker.mapper.ParcelMapper;
import com.hotel.parceltracker.repository.GuestRepository;
import com.hotel.parceltracker.repository.ParcelRepository;
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
class ParcelServiceImplTest {

    private static final Long EXISTING_PARCEL_ID = 1L;
    private static final Long NON_EXISTING_PARCEL_ID = 999L;
    private static final Long EXISTING_GUEST_ID = 1L;
    private static final String PACKAGE_DESCRIPTION = "Package Description";
    private static final String UPDATED_DESCRIPTION = "Updated Description";

    @Mock
    private ParcelRepository parcelRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private ParcelMapper parcelMapper;

    @InjectMocks
    private ParcelServiceImpl parcelService;

    private Parcel parcel;
    private ParcelDto parcelDto;
    private Guest guest;

    @BeforeEach
    void setUp() {
        guest = new Guest();
        guest.setId(EXISTING_GUEST_ID);
        guest.setName("John Doe");
        guest.setStatus(GuestStatus.CHECKED_IN);
        guest.setCheckInTime(LocalDateTime.now().minusDays(1));

        parcel = new Parcel();
        parcel.setId(EXISTING_PARCEL_ID);
        parcel.setGuest(guest);
        parcel.setDescription(PACKAGE_DESCRIPTION);
        parcel.setReceivedDate(LocalDateTime.now().minusDays(1));
        parcel.setStatus(ParcelStatus.PENDING);

        parcelDto = new ParcelDto();
        parcelDto.setId(EXISTING_PARCEL_ID);
        parcelDto.setGuestId(EXISTING_GUEST_ID);
        parcelDto.setDescription(PACKAGE_DESCRIPTION);
        parcelDto.setReceivedDate(parcel.getReceivedDate());
        parcelDto.setStatus(ParcelStatus.PENDING);
    }

    @Test
    void testCreateParcel_Success() {
        when(guestRepository.findById(EXISTING_GUEST_ID)).thenReturn(Optional.of(guest));
        when(parcelMapper.toEntity(any(ParcelDto.class))).thenReturn(parcel);
        when(parcelRepository.save(any(Parcel.class))).thenReturn(parcel);
        when(parcelMapper.toDto(any(Parcel.class))).thenReturn(parcelDto);

        ParcelDto result = parcelService.create(parcelDto);

        assertNotNull(result);
        assertEquals(EXISTING_PARCEL_ID, result.getId());
        assertEquals(PACKAGE_DESCRIPTION, result.getDescription());
        verify(parcelRepository, times(1)).save(any(Parcel.class));
    }

    @Test
    void testFindById_Success() {
        when(parcelRepository.findById(EXISTING_PARCEL_ID)).thenReturn(Optional.of(parcel));
        when(parcelMapper.toDto(any(Parcel.class))).thenReturn(parcelDto);

        ParcelDto result = parcelService.findById(EXISTING_PARCEL_ID);

        assertNotNull(result);
        assertEquals(EXISTING_PARCEL_ID, result.getId());
        verify(parcelRepository, times(1)).findById(EXISTING_PARCEL_ID);
    }

    @Test
    void testFindById_NotFound() {
        when(parcelRepository.findById(NON_EXISTING_PARCEL_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parcelService.findById(NON_EXISTING_PARCEL_ID));
        verify(parcelRepository, times(1)).findById(NON_EXISTING_PARCEL_ID);
    }

    @Test
    void testUpdateParcel_Success() {
        ParcelDto updatedDto = new ParcelDto();
        updatedDto.setId(EXISTING_PARCEL_ID);
        updatedDto.setDescription(UPDATED_DESCRIPTION);

        when(parcelRepository.findById(EXISTING_PARCEL_ID)).thenReturn(Optional.of(parcel));
        when(parcelRepository.save(any(Parcel.class))).thenReturn(parcel);
        when(parcelMapper.toDto(any(Parcel.class))).thenReturn(updatedDto);

        ParcelDto result = parcelService.update(EXISTING_PARCEL_ID, updatedDto);

        assertNotNull(result);
        assertEquals(UPDATED_DESCRIPTION, result.getDescription());
        verify(parcelRepository, times(1)).save(any(Parcel.class));
    }

    @Test
    void testUpdateParcel_NotFound() {
        when(parcelRepository.findById(NON_EXISTING_PARCEL_ID)).thenReturn(Optional.empty());

        ParcelDto dto = new ParcelDto();
        dto.setDescription(UPDATED_DESCRIPTION);

        assertThrows(ResourceNotFoundException.class, () -> parcelService.update(NON_EXISTING_PARCEL_ID, dto));
        verify(parcelRepository, times(1)).findById(NON_EXISTING_PARCEL_ID);
    }

    @Test
    void testDeleteParcel_Success() {
        when(parcelRepository.findById(EXISTING_PARCEL_ID)).thenReturn(Optional.of(parcel));
        doNothing().when(parcelRepository).delete(any(Parcel.class));

        parcelService.delete(EXISTING_PARCEL_ID);

        verify(parcelRepository, times(1)).delete(any(Parcel.class));
    }

    @Test
    void testDeleteParcel_NotFound() {
        when(parcelRepository.findById(NON_EXISTING_PARCEL_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parcelService.delete(NON_EXISTING_PARCEL_ID));

        verify(parcelRepository, times(1)).findById(NON_EXISTING_PARCEL_ID);
    }

    @Test
    void testMarkParcelAsPickedUp_Success() {
        when(parcelRepository.findById(EXISTING_PARCEL_ID)).thenReturn(Optional.of(parcel));
        when(parcelRepository.save(any(Parcel.class))).thenAnswer(invocation -> {
            Parcel updatedParcel = invocation.getArgument(0);
            updatedParcel.setStatus(ParcelStatus.PICKED_UP);
            return updatedParcel;
        });

        ParcelDto updatedParcelDto = new ParcelDto();
        updatedParcelDto.setId(EXISTING_PARCEL_ID);
        updatedParcelDto.setStatus(ParcelStatus.PICKED_UP);

        when(parcelMapper.toDto(any(Parcel.class))).thenReturn(updatedParcelDto);

        ParcelDto result = parcelService.markAsPickedUp(EXISTING_PARCEL_ID);

        assertEquals(ParcelStatus.PICKED_UP, result.getStatus());
        verify(parcelRepository, times(1)).findById(EXISTING_PARCEL_ID);
        verify(parcelRepository, times(1)).save(any(Parcel.class));
    }


    @Test
    void testMarkParcelAsPickedUp_AlreadyPickedUp() {
        parcel.setStatus(ParcelStatus.PICKED_UP);

        when(parcelRepository.findById(EXISTING_PARCEL_ID)).thenReturn(Optional.of(parcel));

        assertThrows(BadRequestException.class, () -> parcelService.markAsPickedUp(EXISTING_PARCEL_ID));

        verify(parcelRepository, times(1)).findById(EXISTING_PARCEL_ID);
    }

    @Test
    void testMarkParcelAsPickedUp_NotFound() {
        when(parcelRepository.findById(NON_EXISTING_PARCEL_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parcelService.markAsPickedUp(NON_EXISTING_PARCEL_ID));

        verify(parcelRepository, times(1)).findById(NON_EXISTING_PARCEL_ID);
    }

    @Test
    void testSearch_Success() {
        ParcelFilter filter = new ParcelFilter();
        filter.setGuestId(EXISTING_GUEST_ID);

        List<Parcel> parcelList = List.of(parcel);

        when(parcelRepository.findAll(any(Specification.class))).thenReturn(parcelList);
        when(parcelMapper.toDto(parcel)).thenReturn(parcelDto);

        List<ParcelDto> result = parcelService.search(filter);

        assertEquals(1, result.size());
        assertEquals(PACKAGE_DESCRIPTION, result.get(0).getDescription());

        verify(parcelRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void testSearch_EmptyFilter() {
        ParcelFilter emptyFilter = new ParcelFilter();

        when(parcelRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<ParcelDto> result = parcelService.search(emptyFilter);

        assertTrue(result.isEmpty());
        verify(parcelRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void testSearch_WithMultipleCriteria() {
        ParcelFilter filter = new ParcelFilter();
        filter.setStatus(ParcelStatus.PENDING);

        List<Parcel> parcelList = List.of(parcel);
        when(parcelRepository.findAll(any(Specification.class))).thenReturn(parcelList);
        when(parcelMapper.toDto(parcel)).thenReturn(parcelDto);

        List<ParcelDto> result = parcelService.search(filter);

        assertEquals(1, result.size());
        assertEquals(PACKAGE_DESCRIPTION, result.get(0).getDescription());
        assertEquals(ParcelStatus.PENDING, result.get(0).getStatus());

        verify(parcelRepository, times(1)).findAll(any(Specification.class));
    }
}


