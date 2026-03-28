package com.capfer.hotel.booking.payments.stripe.mapper;

import com.capfer.hotel.booking.entities.Booking;
import com.capfer.hotel.booking.entities.PaymentEntity;
import com.capfer.hotel.booking.enums.PaymentStatus;
import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//@Component
public class PaymentMapper {

//    public PaymentEntity toEntity(PaymentIntent intent, Booking booking) {
//        return PaymentEntity.builder()
//                .stripePaymentIntentId(intent.getId())
//                .paymentStatus(PaymentStatus.PENDING) // Initial state
//                .amount(BigDecimal.valueOf(intent.getAmount() / 100.0))
//                .(booking)
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
}
