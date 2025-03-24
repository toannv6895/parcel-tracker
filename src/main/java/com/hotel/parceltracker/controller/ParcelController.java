package com.hotel.parceltracker.controller;

import com.hotel.parceltracker.dto.ParcelDto;
import com.hotel.parceltracker.dto.request.ParcelFilter;
import com.hotel.parceltracker.service.ParcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * REST controller for managing parcels.
 */
@RestController
@RequestMapping("/api/parcels")
@Tag(name = "Parcel", description = "APIs for managing parcels in the hotel")
@RequiredArgsConstructor
public class ParcelController {
    private static final Logger logger = LoggerFactory.getLogger(ParcelController.class);

    private final ParcelService parcelService;

    @PostMapping
    @Operation(summary = "Create a new parcel", description = "Creates a new parcel for a checked-in guest")
    @ApiResponse(responseCode = "201", description = "Parcel created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., guest is checked out)")
    public ResponseEntity<ParcelDto> createParcel(@RequestBody ParcelDto parcelDto) {
        logger.info("Received request to create parcel for guest ID: {}", parcelDto.getGuestId());
        ParcelDto createdParcel = parcelService.create(parcelDto);
        return new ResponseEntity<>(createdParcel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parcel by ID", description = "Fetches a parcel by its ID")
    @ApiResponse(responseCode = "200", description = "Parcel found")
    @ApiResponse(responseCode = "404", description = "Parcel not found")
    public ResponseEntity<ParcelDto> getParcelById(@PathVariable Long id) {
        logger.info("Received request to fetch parcel with id: {}", id);
        ParcelDto parcel = parcelService.findById(id);
        return ResponseEntity.ok(parcel);
    }

    @GetMapping
    @Operation(summary = "Get all parcels", description = "Fetches all parcels")
    @ApiResponse(responseCode = "200", description = "List of parcels retrieved")
    public ResponseEntity<Page<ParcelDto>> getAllParcels(
            @PageableDefault(size = 20) Pageable pageable) {
        logger.info("Received request to fetch all parcels");
        Page<ParcelDto> parcels = parcelService.findAll(pageable);
        return ResponseEntity.ok(parcels);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update parcel", description = "Updates an existing parcel's details")
    @ApiResponse(responseCode = "200", description = "Parcel updated successfully")
    @ApiResponse(responseCode = "404", description = "Parcel not found")
    public ResponseEntity<ParcelDto> updateParcel(@PathVariable Long id, @RequestBody ParcelDto parcelDto) {
        logger.info("Received request to update parcel with id: {}", id);
        ParcelDto updatedParcel = parcelService.update(id, parcelDto);
        return ResponseEntity.ok(updatedParcel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete parcel", description = "Deletes a parcel by its ID")
    @ApiResponse(responseCode = "204", description = "Parcel deleted successfully")
    @ApiResponse(responseCode = "404", description = "Parcel not found")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        logger.info("Received request to delete parcel with id: {}", id);
        parcelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search parcels", description = "Searches parcels based on filter criteria")
    @ApiResponse(responseCode = "200", description = "List of parcels matching the filter")
    public ResponseEntity<List<ParcelDto>> searchParcels(@ModelAttribute ParcelFilter filter) {
        logger.info("Received request to search parcels with filter: {}", filter);
        List<ParcelDto> parcels = parcelService.search(filter);
        return ResponseEntity.ok(parcels);
    }

    @PutMapping("/{id}/pickup")
    @Operation(summary = "Mark parcel as picked up", description = "Marks a parcel as picked up by the guest")
    @ApiResponse(responseCode = "200", description = "Parcel marked as picked up")
    @ApiResponse(responseCode = "400", description = "Parcel already picked up")
    @ApiResponse(responseCode = "404", description = "Parcel not found")
    public ResponseEntity<ParcelDto> markParcelAsPickedUp(@PathVariable Long id) {
        logger.info("Received request to mark parcel with id: {} as picked up", id);
        ParcelDto updatedParcel = parcelService.markAsPickedUp(id);
        return ResponseEntity.ok(updatedParcel);
    }
}