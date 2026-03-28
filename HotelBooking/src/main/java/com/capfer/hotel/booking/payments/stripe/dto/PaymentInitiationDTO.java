package com.capfer.hotel.booking.payments.stripe.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentInitiationDTO {
    private String stripePaymentIntentId; // Internal ID (pi_...)
    private String clientSecret;          // Frontend Secret (pi_..._secret_...)
}

