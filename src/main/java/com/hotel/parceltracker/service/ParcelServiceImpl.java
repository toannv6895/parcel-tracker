package com.hotel.parceltracker.service;

import com.hotel.parceltracker.dto.ParcelDto;
import com.hotel.parceltracker.dto.request.ParcelFilter;
import com.hotel.parceltracker.entity.Guest;
import com.hotel.parceltracker.entity.GuestStatus;
import com.hotel.parceltracker.entity.Parcel;
import com.hotel.parceltracker.entity.ParcelStatus;
import com.hotel.parceltracker.exception.BadRequestException;
import com.hotel.parceltracker.exception.ResourceNotFoundException;
import com.hotel.parceltracker.mapper.ParcelMapper;
import com.hotel.parceltracker.repository.GuestRepository;
import com.hotel.parceltracker.repository.ParcelRepository;
import com.hotel.parceltracker.specification.ParcelSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ParcelService for managing parcels.
 */
@Service
@RequiredArgsConstructor
public class ParcelServiceImpl implements ParcelService {
    private static final Logger logger = LoggerFactory.getLogger(ParcelServiceImpl.class);
    private final ParcelRepository parcelRepository;
    private final GuestRepository guestRepository;
    private final ParcelMapper parcelMapper;

    @Override
    public ParcelDto create(ParcelDto dto) {
        logger.info("Creating new parcel for guest with ID: {}", dto.getGuestId());
        Guest guest = guestRepository.findById(dto.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + dto.getGuestId()));
        if (guest.getStatus() != GuestStatus.CHECKED_IN) {
            throw new BadRequestException("Cannot accept parcel for a checked-out guest");
        }
        Parcel parcel = parcelMapper.toEntity(dto);
        parcel.setGuest(guest);
        parcel.setReceivedDate(LocalDateTime.now());
        parcel.setStatus(ParcelStatus.PENDING);
        return parcelMapper.toDto(parcelRepository.save(parcel));
    }

    @Override
    public ParcelDto findById(Long id) {
        logger.info("Fetching parcel with id: {}", id);
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + id));
        return parcelMapper.toDto(parcel);
    }

    @Override
    public List<ParcelDto> findAll() {
        logger.info("Fetching all parcels");
        return parcelRepository.findAll().stream()
                .map(parcelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParcelDto update(Long id, ParcelDto dto) {
        logger.info("Updating parcel with id: {}", id);
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + id));
        parcel.setDescription(dto.getDescription());
        return parcelMapper.toDto(parcelRepository.save(parcel));
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting parcel with id: {}", id);
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + id));
        parcelRepository.delete(parcel);
    }

    @Override
    public List<ParcelDto> search(ParcelFilter filter) {
        logger.info("Searching parcels with filter: {}", filter);
        Specification<Parcel> spec = ParcelSpecification.withFilter(filter);
        return parcelRepository.findAll(spec).stream()
                .map(parcelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParcelDto markAsPickedUp(Long id) {
        logger.info("Marking parcel with id: {} as picked up", id);
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + id));
        if (parcel.getStatus() == ParcelStatus.PICKED_UP) {
            throw new BadRequestException("Parcel is already picked up");
        }
        parcel.setStatus(ParcelStatus.PICKED_UP);
        return parcelMapper.toDto(parcelRepository.save(parcel));
    }
}