package com.capfer.hotel.booking.services;

import com.capfer.hotel.booking.dtos.BookingDTO;
import com.capfer.hotel.booking.dtos.ResponseDTO;

public interface BookingService {

    ResponseDTO getAllBookings();

    ResponseDTO createBooking(BookingDTO bookingDTO);

    ResponseDTO findBookingByReferenceNumber(String bookingReference);

    ResponseDTO updateBooking(BookingDTO bookingDTO);
}
