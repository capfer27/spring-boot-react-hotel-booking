package com.capfer.hotel.booking.exceptions;

public class NameValueRequiredException extends RuntimeException {

    public NameValueRequiredException(String message) {
        super(message);
    }
}
