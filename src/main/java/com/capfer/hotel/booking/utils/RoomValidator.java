package com.capfer.hotel.booking.utils;

import com.capfer.hotel.booking.enums.RoomType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public final class RoomValidator {

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
}
