package com.capfer.hotel.booking.payments.stripe.service;

import com.capfer.hotel.booking.dtos.PaymentDTO;
import com.capfer.hotel.booking.enums.PaymentStatus;
import com.capfer.hotel.booking.repositories.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Note: This endpoint is not being used.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void handleStripeWebhook(Event event) {
        // Extract the PaymentIntent object from the event
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElseThrow(() -> new IllegalArgumentException("Invalid Event Data"));

        // Idempotency Check: Look up the existing payment record
        paymentRepository.findByStripePaymentIntentId(intent.getId())
                .ifPresentOrElse(
                        payment -> {
                            // Update status if not already completed
                            var paymentDTO = modelMapper.map(payment, PaymentDTO.class);
                            if (paymentDTO.getPaymentStatus() != PaymentStatus.COMPLETED) {
                                paymentDTO.setPaymentStatus(PaymentStatus.COMPLETED);
                                paymentRepository.save(payment);
                                log.info("Payment {} updated via webhook.", intent.getId());
                            }
                        },
                        () -> log.warn("Received webhook for unknown PaymentIntent: {}", intent.getId())
                );
    }
}
