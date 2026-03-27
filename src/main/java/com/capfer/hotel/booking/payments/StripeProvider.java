package com.capfer.hotel.booking.payments;

import com.capfer.hotel.booking.payments.exceptions.PaymentException;
import com.capfer.hotel.booking.payments.stripe.dto.PaymentInitiationDTO;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeProvider implements PaymentProvider {

    @Value("${stripe.api.public-key}")
    private String stripePublicKey;

    @Value("${stripe.api.secret-key}")
    private String stripeSecretKey;

    @Override
    public PaymentInitiationDTO createPaymentIntent(Long amountInCents, PaymentCurrency paymentCurrency, String bookingReference) {
        Stripe.apiKey = stripeSecretKey;
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(paymentCurrency.getCurrency())
                    .putMetadata("bookingReference", bookingReference)
                    .build();

            // Define RequestOptions with the idempotency key
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(bookingReference) // Crucial for concurrency
                    .build();

            //Pass options to the create method
            PaymentIntent paymentIntent = PaymentIntent.create(params, options);

            // For idempotency, we need the actual ID (pi_...) for DB storage,
            // while the client needs the ClientSecret to finish the payment.
            return PaymentInitiationDTO.builder()
                    .stripePaymentIntentId(paymentIntent.getId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .build();
        } catch (Exception e) {
            log.error("Stripe payment initiation failed for ref: {}", bookingReference, e);
            throw new PaymentException("Could not initialize payment");
        }
    }
}
