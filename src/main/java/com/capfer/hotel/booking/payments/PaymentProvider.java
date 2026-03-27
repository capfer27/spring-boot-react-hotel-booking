package com.capfer.hotel.booking.payments;

import com.capfer.hotel.booking.payments.stripe.dto.PaymentInitiationDTO;
import com.capfer.hotel.booking.payments.stripe.dto.PaymentRequest;

import java.math.BigDecimal;

public interface PaymentProvider {
    //String createPaymentIntent(PaymentRequest paymentRequest);
    PaymentInitiationDTO createPaymentIntent(Long amountInCents, PaymentCurrency paymentCurrency, String bookingReference);
    //void handleWebhook(String payload, String sigHeader);
}
