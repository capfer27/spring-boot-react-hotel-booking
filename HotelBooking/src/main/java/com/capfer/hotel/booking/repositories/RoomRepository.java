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

    /**
     * Notes:
     * 1. :roomType IS NULL: If you pass null from your Java code, this part becomes TRUE.
     * Because it is joined with an OR, the second part (r.roomType = :roomType) is ignored,
     * effectively disabling the room type filter.
     *
     * 2. r.roomType = :roomType: If you pass a specific RoomType, the first part is FALSE,
     * forcing the query to match only rooms that strictly equal your provided type.
     *
     * 3. Optimize for Overlapping Dates
     * A common issue in booking systems is the "same-day" conflict (e.g., one guest checks out at 10 AM, another checks in at 2 PM).
     * The current query uses <= and >=.
     * To allow a room to be booked on the same day someone else leaves, use exclusive boundaries: < and >
     *
     * @param checkInDate
     * @param checkOutDate
     * @param roomType
     * @return
     */
    @Query("""
        SELECT r FROM Room r
        WHERE NOT EXISTS (
            SELECT 1 FROM Booking b
            WHERE b.room = r
            AND b.checkInDate < :checkOutDate
            AND b.checkOutDate > :checkInDate
            AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
        )
        AND (:roomType IS NULL OR r.roomType = :roomType)
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

    List<Room> findByRoomType(RoomType roomType);

    List<Room> findByRoomTypeAndIdNotIn(RoomType roomType, List<Long> bookedRoomIds);

}
