package com.hotel.parceltracker.controller;

import com.hotel.parceltracker.dto.GuestDto;
import com.hotel.parceltracker.dto.request.GuestFilter;
import com.hotel.parceltracker.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing hotel guests.
 */
@RestController
@RequestMapping("/api/guests")
@Tag(name = "Guest", description = "APIs for managing hotel guests")
@RequiredArgsConstructor
public class GuestController {
    private static final Logger logger = LoggerFactory.getLogger(GuestController.class);
    private final GuestService guestService;

    @PostMapping
    @Operation(summary = "Create a new guest", description = "Creates a new guest and sets status to CHECKED_IN")
    @ApiResponse(responseCode = "201", description = "Guest created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<GuestDto> createGuest(@Valid @RequestBody GuestDto guestDto) {
        logger.info("Received request to create guest: {}", guestDto.getName());
        GuestDto createdGuest = guestService.create(guestDto);
        return new ResponseEntity<>(createdGuest, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get guest by ID", description = "Fetches a guest by their ID")
    @ApiResponse(responseCode = "200", description = "Guest found")
    @ApiResponse(responseCode = "404", description = "Guest not found")
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Long id) {
        logger.info("Received request to fetch guest with id: {}", id);
        GuestDto guest = guestService.findById(id);
        return ResponseEntity.ok(guest);
    }

    @GetMapping
    @Operation(summary = "Get all guests", description = "Fetches all guests")
    @ApiResponse(responseCode = "200", description = "List of guests retrieved")
    public ResponseEntity<Page<GuestDto>> getAllGuests(
            @PageableDefault(size = 20) Pageable pageable) {
        logger.info("Received request to fetch all guests");
        Page<GuestDto> guests = guestService.findAll(pageable);
        return ResponseEntity.ok(guests);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update guest", description = "Updates an existing guest's details")
    @ApiResponse(responseCode = "200", description = "Guest updated successfully")
    @ApiResponse(responseCode = "404", description = "Guest not found")
    public ResponseEntity<GuestDto> updateGuest(@PathVariable Long id, @RequestBody GuestDto guestDto) {
        logger.info("Received request to update guest with id: {}", id);
        GuestDto updatedGuest = guestService.update(id, guestDto);
        return ResponseEntity.ok(updatedGuest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete guest", description = "Deletes a guest by their ID")
    @ApiResponse(responseCode = "204", description = "Guest deleted successfully")
    @ApiResponse(responseCode = "404", description = "Guest not found")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        logger.info("Received request to delete guest with id: {}", id);
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search guests", description = "Searches guests based on filter criteria")
    @ApiResponse(responseCode = "200", description = "List of guests matching the filter")
    public ResponseEntity<List<GuestDto>> searchGuests(@ModelAttribute GuestFilter filter) {
        logger.info("Received request to search guests with filter: {}", filter);
        List<GuestDto> guests = guestService.search(filter);
        return ResponseEntity.ok(guests);
    }

    @PutMapping("/{id}/checkout")
    @Operation(summary = "Check out a guest", description = "Marks a guest as checked out")
    @ApiResponse(responseCode = "200", description = "Guest checked out successfully")
    @ApiResponse(responseCode = "400", description = "Guest already checked out")
    @ApiResponse(responseCode = "404", description = "Guest not found")
    public ResponseEntity<GuestDto> checkOutGuest(@PathVariable Long id) {
        logger.info("Received request to check out guest with id: {}", id);
        GuestDto checkedOutGuest = guestService.checkOut(id);
        return ResponseEntity.ok(checkedOutGuest);
    }
}