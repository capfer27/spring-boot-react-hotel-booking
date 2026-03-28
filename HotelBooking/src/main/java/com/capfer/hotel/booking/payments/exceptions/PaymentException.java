package com.capfer.hotel.booking.payments.exceptions;

public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }
}
