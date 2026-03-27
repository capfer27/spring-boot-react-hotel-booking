package com.capfer.hotel.booking.payments;

import lombok.Getter;

@Getter
public enum PaymentCurrency {
    USD("usd"),
    EUR("eur");

    private final String currency;

    PaymentCurrency(String currency) {
        this.currency = currency;
    }

}
