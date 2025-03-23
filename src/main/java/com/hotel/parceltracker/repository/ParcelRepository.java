package com.hotel.parceltracker.repository;

import com.hotel.parceltracker.entity.Parcel;
import com.hotel.parceltracker.entity.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParcelRepository extends JpaRepository<Parcel, Long>, JpaSpecificationExecutor<Parcel> {
    boolean existsByGuestIdAndStatusNot(Long guestId, ParcelStatus status);
}