package com.capfer.hotel.booking.entities;

import com.capfer.hotel.booking.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "rooms")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DynamicUpdate
public class Room {

    // NOTE: We intentionally do NOT have a List<Booking> here.
    //Why? A popular room will have hundreds of bookings over a year.
    //Loading a Room would force Hibernate to manage a massive list in memory, killing performance.
    //The Solution: Keep it "Unidirectional" from the Booking side.
    //If you need to find bookings for a room, use a Repository Query.

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Room Number must be at least 1")
    @Column(name = "room_number", unique = true)
    private Integer roomNumber;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @Column(name = "price_per_night")
    @DecimalMin(value = "0.1", message = "Price per night is required")
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String description; // additional data for the room

    @Column(name = "image_url")
    private String imageUrl; // this will hold the room picture

    // Read-only mapping for the Postgres `search_vector` column.
    // It's populated/maintained by the database (trigger or generated column).
    // Marking insertable=false, updatable=false prevents JPA from including it in INSERT/UPDATE statements
    // so it won't break inserts or updates coming from the application.
    @Column(name = "search_vector", insertable = false, updatable = false, columnDefinition = "TEXT")
    @Setter(AccessLevel.NONE) // Lombok will not generate a setter for this field, keeping it read-only in the application.
    @Getter
    private String searchVector;

//    // Provide a getter only to keep the field read-only in the application.
//    public String getSearchVector() {
//        return searchVector;
//    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id) && Objects.equals(roomNumber, room.roomNumber) && roomType == room.roomType && Objects.equals(pricePerNight, room.pricePerNight) && Objects.equals(capacity, room.capacity) && Objects.equals(description, room.description) && Objects.equals(imageUrl, room.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomNumber, roomType, pricePerNight, capacity, description, imageUrl);
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber=" + roomNumber +
                ", roomType=" + roomType +
                ", pricePerNight=" + pricePerNight +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
