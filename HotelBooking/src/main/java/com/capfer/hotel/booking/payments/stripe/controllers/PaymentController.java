package com.capfer.hotel.booking.payments.stripe.controllers;

import com.capfer.hotel.booking.payments.stripe.dto.PaymentInitiationDTO;
import com.capfer.hotel.booking.payments.stripe.dto.PaymentRequest;
import com.capfer.hotel.booking.payments.stripe.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(path = "/pay")
    public ResponseEntity<PaymentInitiationDTO> createPaymentIntent(@RequestBody PaymentRequest request) {
//        return ResponseEntity.ok(paymentService.handleBookingPayment(request));
        return ResponseEntity.ok(paymentService.createPaymentIntent(request));
    }

    @PostMapping(path = "/update")
    public void updatePaymentBooking(@RequestBody PaymentRequest request) {
         paymentService.updateBookingPayment(request);
    }
}
