package com.capfer.hotel.booking.repositories;

import com.capfer.hotel.booking.dtos.BookingDTO;
import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.entities.Booking;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.StructuredTaskScope;

/**
 * How to check Availability (The Repository)
 * Note: Since we didn't map the list in the Room entity,
 * use a query to check for overlaps.
 * This is the most efficient way to see if a room is "free":
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // AI Suggested Query:
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.room.id = :roomId
            AND b.checkInDate < :checkOutDate
            AND b.checkOutDate > :checkInDate
            AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
        """)
    boolean isRoomOccupied(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    //AI Suggested Query with effective date overlap check and GIN index usage:
    // Overlap logic: Start < End AND End > Start
    @Query(value = """
            SELECT NOT EXISTS (
             SELECT 1
             FROM bookings
             WHERE room_id = :roomId
             AND booking_status IN ('BOOKED', 'CHECKED_IN')
             AND check_in_date < :checkOutDate
             AND check_out_date > :checkInDate
           )                                               
        """, nativeQuery = true
    )
    boolean isRoomAvailable(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    List<Booking> findByUserId(Long userId);

    Optional<Booking> findByBookingReference(String bookingReference);

    @Query("SELECT b.room.id FROM Booking b WHERE " +
            "b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    List<Long> findBookedRoomIds(LocalDate checkInDate, LocalDate checkOutDate);

    /**
     * Retrieves all bookings ordered by ID in descending order.
     * <p>
     * This method utilizes {@link StructuredTaskScope} to handle data retrieval using
     * Structured Concurrency. Note that because forked subtasks run in separate threads,
     * they do not inherit the parent thread's Hibernate Session/Transaction.
     * </p>
     * <p>
     * <b>Pre-condition for Lazy Loading:</b>
     * To avoid {@code LazyInitializationException}, the repository call must utilize
     * "Join Fetch" (eager loading) to ensure the User and Room entities are fully
     * initialized before the subtask completes and the session closes.
     * </p>
     *
     * @return A {@link ResponseDTO} containing a list of {@link BookingDTO}s or an error status.
     * @throws InterruptedException if the structured scope is interrupted.
     * @throws ExecutionException if the database operation fails.
     */

    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.room ORDER BY b.id DESC")
    List<Booking> findAllWithDetails();


}
