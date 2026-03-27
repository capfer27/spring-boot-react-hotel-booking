package com.capfer.hotel.booking.payments.exceptions;

public class BookingAlreadyCompletedException extends RuntimeException {

    public BookingAlreadyCompletedException(String s) {
        super(s);
    }
}
