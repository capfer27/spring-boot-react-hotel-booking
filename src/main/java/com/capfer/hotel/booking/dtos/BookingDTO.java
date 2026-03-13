package com.capfer.hotel.booking.dtos;

import com.capfer.hotel.booking.entities.Room;
import com.capfer.hotel.booking.entities.User;
import com.capfer.hotel.booking.enums.BookingStatus;
import com.capfer.hotel.booking.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDTO {
    private Long id;

    private UserDTO user;

    private RoomDTO room;

    private Long roomId;

    private PaymentStatus paymentStatus;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BigDecimal totalPrice;

    private String bookingReference;

    private LocalDateTime createdAt;

    private BookingStatus bookingStatus;
}
