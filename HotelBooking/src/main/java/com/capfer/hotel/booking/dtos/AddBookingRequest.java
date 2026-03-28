package com.capfer.hotel.booking.dtos;

import com.capfer.hotel.booking.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record AddBookingRequest(
    @NotNull Integer roomNumber,
    @NotNull RoomType roomType,
    @NotNull BigDecimal pricePerNight,
    @NotNull Integer capacity,
    @NotBlank String description,
    @NotNull MultipartFile imageFile
) {
}
