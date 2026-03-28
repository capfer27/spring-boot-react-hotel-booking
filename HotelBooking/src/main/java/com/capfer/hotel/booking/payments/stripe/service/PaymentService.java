package com.capfer.hotel.booking.payments.stripe.service;

import com.capfer.hotel.booking.dtos.NotificationDTO;
import com.capfer.hotel.booking.entities.Booking;
import com.capfer.hotel.booking.entities.PaymentEntity;
import com.capfer.hotel.booking.enums.NotificationType;
import com.capfer.hotel.booking.enums.PaymentGateway;
import com.capfer.hotel.booking.enums.PaymentStatus;
import com.capfer.hotel.booking.exceptions.NotFoundException;
import com.capfer.hotel.booking.payments.PaymentCurrency;
import com.capfer.hotel.booking.payments.PaymentProvider;
import com.capfer.hotel.booking.payments.exceptions.BookingAlreadyCompletedException;
import com.capfer.hotel.booking.payments.stripe.dto.PaymentInitiationDTO;
import com.capfer.hotel.booking.payments.stripe.dto.PaymentRequest;
import com.capfer.hotel.booking.repositories.BookingRepository;
import com.capfer.hotel.booking.repositories.PaymentRepository;
import com.capfer.hotel.booking.services.NotificationService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    /**
     * Separation of Concerns: PaymentService manages your database; StripeProvider manages the external API.
     * Idempotency: You can easily add a payment_intent_id column to your Order table to prevent double-charging.
     * Security: By returning only the clientSecret to your frontend, you ensure sensitive card data
     * never touches your server (keeping you PCI compliant).
     */

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final PaymentProvider paymentProvider;

    public PaymentInitiationDTO createPaymentIntent(PaymentRequest request) {
        // Stripe expects amounts in cents (e.g., $10.00 = 1000)
        long amountInCents = request.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        PaymentInitiationDTO intent = paymentProvider.createPaymentIntent(
                amountInCents, PaymentCurrency.EUR, request.getBookingReference()
        );

        return intent;
    }

    @Transactional
    public void updateBookingPayment(PaymentRequest paymentRequest) {

        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking Reference not found"));

        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BookingAlreadyCompletedException("Payment already made for this booking");
        }

        // Stripe expects amounts in cents (e.g., $10.00 = 1000)
//        long amountInCents = paymentRequest.getAmount()
//                .multiply(BigDecimal.valueOf(100))
//                .longValue();
//
//        PaymentInitiationDTO paymentInitiationDTO = paymentProvider.createPaymentIntent(
//                amountInCents,
//                PaymentCurrency.EUR,
//                bookingReference
//        );

        // NOTES: This check is called
        // To handle concurrent requests (where two threads check exists at the same time and both try to insert),
        // you cannot rely on if (exists). You must let the Database Unique Constraint you created via Flyway do the heavy lifting.
        //In a high-concurrency environment, the "Check-then-Act" pattern is vulnerable to a Race Condition.
        // Instead, use the "Attempt-and-Catch" pattern.

        // TODO: also store the payment intent id in the db for idempotency
        boolean isSuccess = paymentRequest.isSuccess();

        // The Atomic "Upsert" Pattern
        // Use the ID for your Database Idempotency Logic
        PaymentEntity payment = getOrCreatePayment(
                paymentRequest.getStripePaymentIntentId(),
                booking,
                paymentRequest,
                isSuccess,
                bookingReference
        );

        log.info("Stripe Payment initialized for booking {}", booking);

        // Send notification
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(booking.getUser().getEmail())
                .notificationType(NotificationType.EMAIL)
                .bookingReference(bookingReference)
                .build();

        booking.setPaymentStatus(payment.getPaymentStatus());
        if (isSuccess) {
            notificationDTO.setSubject("Booking payment was successful");
            notificationDTO.setBody("Congratulations!! Your payment for booking with reference: " + bookingReference + " is successful.");

        } else {
            notificationDTO.setSubject("Booking payment failed");
            notificationDTO.setBody("Your payment for booking with reference: " + bookingReference +
                    " failed with reason: " + paymentRequest.getFailureReason());
        }
        notificationService.sendEmail(notificationDTO);
        bookingRepository.save(booking);

        // Return the Secret so the Controller can send it to React
        //return paymentInitiationDTO.getClientSecret();
    }

    public PaymentEntity getOrCreatePayment(
            String stripePaymentIntentId,
            Booking booking,
            PaymentRequest paymentRequest,
            boolean isSuccess,
            String bookingReference
    ) {
        // 1. Try to find it first (Optimization)
        return paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElseGet(() -> {
                try {
                    // 2. If not found, try to create it
                    PaymentEntity.PaymentEntityBuilder paymentEntityBuilder = PaymentEntity.builder()
                            .paymentDate(LocalDateTime.now())
                            .paymentGateway(PaymentGateway.STRIPE)
                            .amount(paymentRequest.getAmount())
                            .transactionId(paymentRequest.getTransactionId())
                            .paymentStatus(isSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                            .bookingReference(bookingReference)
                            .stripePaymentIntentId(stripePaymentIntentId)
                            .user(booking.getUser());

                    if (!isSuccess) {
                        paymentEntityBuilder.failureReason(paymentRequest.getFailureReason());
                    }
                    return paymentRepository.saveAndFlush(paymentEntityBuilder.build());
                } catch (DataIntegrityViolationException e) {
                    // 3. Conflict detected! Another thread saved this stripePaymentIntentId first.
                    log.warn("Concurrent payment attempt detected for Stripe ID: {}. Fetching existing record.", stripePaymentIntentId);
                    // 3. If a concurrent thread beat us to the insert,
                    // the DB constraint triggers this. Just fetch the winner's record.
                    return paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                            .orElseThrow(() -> new IllegalStateException("Concurrency error: Payment lost."));
                }
            });
    }



    // TODO: This method will make sense only when the webhook is implemented
    @Transactional
    public void processWebhookEvent(Event event) {
        // Handle only specific event types
        if ("payment_intent.succeeded".equals(event.getType())) {
            updateStatus(event, PaymentStatus.COMPLETED);
        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            updateStatus(event, PaymentStatus.FAILED);
        }
        // Handle charge.refunded for REFUNDED/REVERSED if needed
    }

    private void updateStatus(Event event, PaymentStatus newStatus) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        paymentRepository.findByStripePaymentIntentId(intent.getId())
                .ifPresent(payment -> {
                    payment.setPaymentStatus(newStatus);
                    //paymentRepository.save(payment);
                });
    }
}
