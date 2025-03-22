package com.hotel.parceltracker.specification;

import com.hotel.parceltracker.dto.request.GuestFilter;
import com.hotel.parceltracker.entity.Guest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for dynamic querying of Guest entities.
 */
public class GuestSpecification {

    /**
     * Creates a Specification based on the provided GuestFilter.
     *
     * @param filter The filter criteria for searching guests.
     * @return A Specification for querying guests.
     */
    public static Specification<Guest> withFilter(GuestFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                // Filter by name (case-insensitive partial match)
                String name = filter.getName();
                if (name != null && !name.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
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