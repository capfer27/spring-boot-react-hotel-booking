package com.capfer.hotel.booking.exceptions;

public class InvalidBookingStateAndDateException extends RuntimeException {

    public InvalidBookingStateAndDateException(String message) {
        super(message);
    }
}
