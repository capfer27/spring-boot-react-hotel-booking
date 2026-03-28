package com.capfer.hotel.booking.utils;

import com.capfer.hotel.booking.dtos.BookingDTO;
import com.capfer.hotel.booking.entities.Booking;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.List;

public final class BookingHelper {

    private BookingHelper(){}

    public static List<BookingDTO> toDTOs(List<Booking> bookings, ModelMapper mapper) {
        return mapper.map(bookings, new TypeToken<List<BookingDTO>>(){}.getType());
    }

    public static List<BookingDTO> toDTOs(List<Booking> bookings, ModelMapper mapper, boolean excludeRequired) {
        List<BookingDTO> bookingDTOS = toDTOs(bookings, mapper);

        if (excludeRequired) {
            bookingDTOS = bookingDTOS.stream()
                .map(bookingDTO -> {
                    bookingDTO.setRoom(null);
                    bookingDTO.setUser(null);
                    return bookingDTO;
                })
                .toList();
        }

        return bookingDTOS;
    }
}
