package com.capfer.hotel.booking.repositories;

import com.capfer.hotel.booking.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

/**
 * How to check Availability (The Repository)
 * Note: Since we didn't map the list in the Room entity,
 * use a query to check for overlaps.
 * This is the most efficient way to see if a room is "free":
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.checkInDate < :out " +
            "AND b.checkOutDate > :in")
    boolean isRoomOccupied(
            @Param("roomId") Long roomId,
            @Param("in") LocalDate checkInDate,
            @Param("out") LocalDate checkOutDate
    );
}
