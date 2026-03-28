package com.capfer.hotel.booking.exceptions;

public class InsufficientAuthorizationException extends RuntimeException {

    public InsufficientAuthorizationException(String message) {
        super(message);
    }
}
