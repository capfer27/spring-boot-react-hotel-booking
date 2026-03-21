package com.capfer.hotel.booking.repositories;

import com.capfer.hotel.booking.entities.Room;
import com.capfer.hotel.booking.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room>
{

    // This query is not optimized for large datasets,
    // but it demonstrates the concept of finding available rooms based on booking dates.
    // Write a custom query method to find available rooms
    // based on check-in and check-out dates and room type
    @Query("""
            SELECT r FROM Room r
            WHERE r.id NOT IN (
                SELECT b.room.id FROM Booking b
                WHERE (b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate)
                AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
            )
            AND (r.roomType IS NULL OR r.roomType = :roomType)
        """)
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomType") String roomType
    );

    // Optimized way to find available rooms based on check-in and check-out dates and room type
    // PostgreSQL often handles NOT EXISTS better than NOT IN when dealing with nulls or large datasets.
    // If performance is still slow, consider refactoring the JPQL/SQL:
//    @Query("""
//            SELECT r FROM Room r
//            WHERE NOT EXISTS (
//                SELECT 1 FROM Booking b
//                WHERE b.room.id = r.id
//                AND (b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate)
//                AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
//            )
//            AND (r.roomType IS NULL OR r.roomType = :roomType)
//        """)
    @Query("""
        SELECT r FROM Room r
        WHERE (r.roomType IS NULL OR r.roomType = :roomType)
        AND NOT EXISTS (
            SELECT 1 FROM Booking b
            WHERE b.room = r
            AND b.checkInDate <= :checkOutDate
            AND b.checkOutDate >= :checkInDate
            AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
        )
    """)
    List<Room> findAvailableRoomsOptimized(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomType") RoomType roomType
    );

    @Query("""
        SELECT r FROM Room r
        WHERE LOWER(CAST(r.roomNumber AS string)) LIKE LOWER(CONCAT('%', :searchParam, '%'))
        OR LOWER(r.roomType) LIKE LOWER(CONCAT('%', :searchParam, '%'))
        OR CAST(r.pricePerNight AS string) LIKE CONCAT('%', :searchParam, '%')
        OR CAST(r.capacity AS string) LIKE CONCAT('%', :searchParam, '%')
        OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchParam, '%'))
    """)
    List<Room> searchRooms(@Param("searchParam") String searchParam);


    @Query("SELECT DISTINCT r.roomType FROM Room r")
    List<RoomType> getAllRoomTypes();

}
