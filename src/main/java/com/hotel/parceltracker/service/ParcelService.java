package com.hotel.parceltracker.service;

import com.hotel.parceltracker.dto.ParcelDto;
import com.hotel.parceltracker.dto.request.ParcelFilter;

import java.util.List;

/**
 * Service interface for managing parcel-related operations.
 */
public interface ParcelService extends BaseService<ParcelDto, Long> {

    /**
     * Searches for parcels based on the provided filter criteria.
     *
     * @param filter The filter criteria for searching parcels.
     * @return A list of ParcelDto objects matching the filter.
     */
    List<ParcelDto> search(ParcelFilter filter);

    /**
     * Marks a parcel as picked up by the guest.
     *
     * @param id The ID of the parcel to mark as picked up.
     * @return The updated ParcelDto object.
     */
    ParcelDto markAsPickedUp(Long id);
}