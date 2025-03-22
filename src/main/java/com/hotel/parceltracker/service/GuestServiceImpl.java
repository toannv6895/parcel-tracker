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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestServiceImpl implements GuestService {
    private static final Logger logger = LoggerFactory.getLogger(GuestServiceImpl.class);

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private GuestMapper guestMapper;

    @Override
    public GuestDto create(GuestDto dto) {
        logger.info("Creating new guest with name: {}", dto.getName());
        Guest guest = guestMapper.toEntity(dto);
        guest.setCheckInTime(LocalDateTime.now());
        guest.setStatus(GuestStatus.CHECKED_IN);
        return guestMapper.toDto(guestRepository.save(guest));
    }

    @Override
    public GuestDto findById(Long id) {
        logger.info("Fetching guest with id: {}", id);
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));
        return guestMapper.toDto(guest);
    }

    @Override
    public List<GuestDto> findAll() {
        logger.info("Fetching all guests");
        return guestRepository.findAll().stream()
                .map(guestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public GuestDto update(Long id, GuestDto dto) {
        logger.info("Updating guest with id: {}", id);
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));
        guest.setName(dto.getName());
        return guestMapper.toDto(guestRepository.save(guest));
    }

    @Override
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