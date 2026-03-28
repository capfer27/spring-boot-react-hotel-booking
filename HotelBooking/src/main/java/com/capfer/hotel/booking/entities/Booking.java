package com.capfer.hotel.booking.entities;

import com.capfer.hotel.booking.enums.BookingStatus;
import com.capfer.hotel.booking.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@DynamicUpdate
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * FetchType.LAZY to prevent Hibernate from loading the entire User object every time
     * we just want to see a booking date.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many bookings can be made for one room (over different dates)
    // Direction: Unidirectional (Booking → Room).
    //Fetching: Always LAZY.
    //Integrity: Use nullable = false on the @JoinColumn because a booking without a room is invalid.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    // unique reference for the booking, can be generated as a UUID or a combination of user and timestamp
    @Column(name = "booking_reference", unique = true, nullable = false)
    private String bookingReference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(user, booking.user) && Objects.equals(room, booking.room) && paymentStatus == booking.paymentStatus && Objects.equals(checkInDate, booking.checkInDate) && Objects.equals(checkOutDate, booking.checkOutDate) && Objects.equals(totalPrice, booking.totalPrice) && Objects.equals(bookingReference, booking.bookingReference) && Objects.equals(createdAt, booking.createdAt) && bookingStatus == booking.bookingStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, room, paymentStatus, checkInDate, checkOutDate, totalPrice, bookingReference, createdAt, bookingStatus);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", paymentStatus=" + paymentStatus +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", totalPrice=" + totalPrice +
                ", bookingReference='" + bookingReference + '\'' +
                ", createdAt=" + createdAt +
                ", bookingStatus=" + bookingStatus +
                '}';
    }
}
