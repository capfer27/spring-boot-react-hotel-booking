package com.capfer.hotel.booking.payments.stripe.controllers;

import com.capfer.hotel.booking.payments.stripe.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Note: This endpoint is not being used as of now.
 */
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentService paymentService;

    //@Value("${stripe.webhook.secret}")
    private String endpointSecret = "failed-webhook-secret-key";

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            // Verify the signature and construct the event
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            // Delegate to service for processing
            paymentService.processWebhookEvent(event);

            return ResponseEntity.ok("Success");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
        }
    }
}

