package com.hotel.parceltracker.specification;

import com.hotel.parceltracker.dto.request.ParcelFilter;
import com.hotel.parceltracker.entity.Parcel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for dynamic querying of Parcel entities.
 */
public class ParcelSpecification {

    /**
     * Creates a Specification based on the provided ParcelFilter.
     *
     * @param filter The filter criteria for searching parcels.
     * @return A Specification for querying parcels.
     */
    public static Specification<Parcel> withFilter(ParcelFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                // Filter by guest ID
                Long guestId = filter.getGuestId();
                if (guestId != null) {
                    predicates.add(cb.equal(root.get("guest").get("id"), guestId));
                }

                // Filter by status
                if (filter.getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), filter.getStatus()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}