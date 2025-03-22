package com.hotel.parceltracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "parcels")
@Getter
@Setter
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(nullable = false)
    private String description;

    @Column(name = "received_date", nullable = false)
    private LocalDateTime receivedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;
}