package com.capfer.hotel.booking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingReference that = (BookingReference) o;
        return Objects.equals(id, that.id) && Objects.equals(referenceNumber, that.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, referenceNumber);
    }

    @Override
    public String toString() {
        return "BookingReference{" +
                "id=" + id +
                ", referenceNumber='" + referenceNumber + '\'' +
                '}';
    }
}
