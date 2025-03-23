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
import com.hotel.parceltracker.specification.ParcelSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @CacheEvict(cacheNames = "parcels", key = "'all_*'")
    @Transactional
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
    @Cacheable(cacheNames = "parcels", key = "#id")
    public ParcelDto findById(Long id) {
        logger.info("Fetching parcel with id: {}", id);
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + id));
        return parcelMapper.toDto(parcel);
    }

    @Override
    @Cacheable(cacheNames = "parcels", key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<ParcelDto> findAll(Pageable pageable) {
        logger.info("Fetching all parcels with pagination");
        return parcelRepository.findAll(pageable).map(parcelMapper::toDto);
    }

    @Override
    @Caching(
            put = @CachePut(cacheNames = "parcels", key = "#id"),
            evict = @CacheEvict(cacheNames = "parcels", key = "'all_*'")
    )
    @Transactional
    public ParcelDto update(Long id, ParcelDto dto) {
        logger.info("Updating parcel with id: {}", id);
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found with id: " + id));
        parcel.setDescription(dto.getDescription());
        return parcelMapper.toDto(parcelRepository.save(parcel));
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "parcels", key = "#id"),
                    @CacheEvict(cacheNames = "parcels", key = "'all_*'")
            }
    )
    @Transactional
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
    @Caching(
            put = @CachePut(cacheNames = "parcels", key = "#id"),
            evict = @CacheEvict(cacheNames = "parcels", key = "'all_*'")
    )
    @Transactional
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