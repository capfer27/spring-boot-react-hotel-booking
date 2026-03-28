package com.capfer.hotel.booking.repositories;

import com.capfer.hotel.booking.entities.BookingReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingReferenceRepository extends JpaRepository<BookingReference, Long> {

    Optional<BookingReference> findByReferenceNumber(String referenceNumber);
}
