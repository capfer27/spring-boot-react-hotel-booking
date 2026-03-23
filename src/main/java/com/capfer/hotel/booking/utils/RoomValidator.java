package com.capfer.hotel.booking.utils;

import com.capfer.hotel.booking.enums.RoomType;
import com.capfer.hotel.booking.exceptions.InvalidBookingStateAndDateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class RoomValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomValidator.class);

    private RoomValidator() {}

    public static boolean isValidRoomNumber(final Integer roomNumber) {
        return roomNumber != null && roomNumber >= 0;
    }

    public static boolean isValidImageFile(final MultipartFile imageFile) {
        return !ObjectUtils.isEmpty(imageFile);
    }

    public static boolean isValidPricePerNight(final BigDecimal price) {
       return !ObjectUtils.isEmpty(price) && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static boolean hasValidCapacity(Integer capacity) {
        return !ObjectUtils.isEmpty(capacity) && capacity > 0;
    }

    public static boolean isValidRoomType(RoomType roomType) {
        return !ObjectUtils.isEmpty(roomType);
    }

    public static boolean hasValidDescription(String description) {
        return StringUtils.hasText(description);
    }

    public static boolean isValidCheckInDate(LocalDate checkInDate) {
        return !checkInDate.isBefore(LocalDate.now());
    }

    public static boolean isValidCheckOutDate(LocalDate checkInDate, LocalDate checkOutDate) {
        return checkOutDate.isAfter(checkInDate);
    }

    public static void validateBooking(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now();

        if (checkIn == null || checkOut == null) {
            throw new InvalidBookingStateAndDateException("Dates cannot be null.");
        } else if (checkIn.isBefore(today)) {
            throw new InvalidBookingStateAndDateException("check in date cannot be before today");
        } else if (!checkOut.isAfter(checkIn)) {
            throw new InvalidBookingStateAndDateException("check-out date cannot be before check-in date");
        } else if (checkIn.equals(checkOut)) {
            throw new InvalidBookingStateAndDateException("check-out date cannot be same as check-in date");
        } else {
            LOGGER.info("Dates are valid. Proceeding to availability check...");
        }
    }

}
