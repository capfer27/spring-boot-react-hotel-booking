package com.capfer.hotel.booking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking_references")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    // This can be a UUID or a combination of user and timestamp
    @Column(name = "reference_number", unique = true, nullable = false)
    private String referenceNumber;
}
