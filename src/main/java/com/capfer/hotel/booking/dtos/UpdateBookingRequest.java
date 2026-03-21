package com.capfer.hotel.booking.dtos;

import com.capfer.hotel.booking.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record UpdateBookingRequest(
    @NotNull Long roomId,
    Integer roomNumber,
    RoomType roomType,
    BigDecimal pricePerNight,
    Integer capacity,
    String description,
    MultipartFile imageFile
) {
}
