package com.hotel.parceltracker.service;

import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.dto.request.GuestFilter;
import com.hotel.parceltracker.entity.Guest;
import com.hotel.parceltracker.entity.GuestStatus;
import com.hotel.parceltracker.exception.BadRequestException;
import com.hotel.parceltracker.exception.ResourceNotFoundException;
import com.hotel.parceltracker.mapper.GuestMapper;
import com.hotel.parceltracker.repository.GuestRepository;
import com.hotel.parceltracker.specification.GuestSpecification;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of GuestService for managing guests.
 */
@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {
    private static final Logger logger = LoggerFactory.getLogger(GuestServiceImpl.class);
    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;

    @Override
    @CacheEvict(cacheNames = "guests", key = "'all_*'")
    public GuestDto create(GuestDto dto) {
        logger.info("Creating new guest with name: {}", dto.getName());
        Guest guest = guestMapper.toEntity(dto);
        guest.setCheckInTime(LocalDateTime.now());
        guest.setStatus(GuestStatus.CHECKED_IN);
        return guestMapper.toDto(guestRepository.save(guest));
    }

    @Override
    @Cacheable(cacheNames = "guests", key = "#id")
    public GuestDto findById(Long id) {
        logger.info("Fetching guest with id: {}", id);
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));
        return guestMapper.toDto(guest);
    }

    @Override
    @Cacheable(cacheNames = "guests", key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<GuestDto> findAll(Pageable pageable) {
        logger.info("Fetching all guests with pagination");
        return guestRepository.findAll(pageable).map(guestMapper::toDto);
    }

    @Override
    @Caching(
            put = @CachePut(cacheNames = "guests", key = "#id"),
            evict = @CacheEvict(cacheNames = "guests", key = "'all_*'")
    )
    public GuestDto update(Long id, GuestDto dto) {
        logger.info("Updating guest with id: {}", id);
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));
        guest.setName(dto.getName());
        return guestMapper.toDto(guestRepository.save(guest));
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "guests", key = "#id"),
                    @CacheEvict(cacheNames = "guests", key = "'all_*'")
            }
    )
    public void delete(Long id) {
        logger.info("Deleting guest with id: {}", id);
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));
        guestRepository.delete(guest);
    }

    @Override
    public List<GuestDto> search(GuestFilter filter) {
        logger.info("Searching guests with filter: {}", filter);
        Specification<Guest> spec = GuestSpecification.withFilter(filter);
        return guestRepository.findAll(spec).stream()
                .map(guestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Caching(
            put = @CachePut(cacheNames = "guests", key = "#id"),
            evict = @CacheEvict(cacheNames = "guests", key = "'all_*'")
    )
    public GuestDto checkOut(Long id) {
        logger.info("Checking out guest with id: {}", id);
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));
        if (guest.getStatus() == GuestStatus.CHECKED_OUT) {
            throw new BadRequestException("Guest is already checked out");
        }
        guest.setStatus(GuestStatus.CHECKED_OUT);
        guest.setCheckOutTime(LocalDateTime.now());
        return guestMapper.toDto(guestRepository.save(guest));
    }
}