package com.capfer.hotel.booking.dtos;

import com.capfer.hotel.booking.enums.PaymentGateway;
import com.capfer.hotel.booking.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {

    private Long id;

    private  BookingDTO bookingDTO;

    private String transactionId;

    private BigDecimal amount;

    private PaymentGateway paymentMethod;

    private LocalDateTime paymentDate;

    private PaymentStatus paymentStatus;

    private String bookingReference;

    private String failureReason;

    private String approvalLink; // payment gateway approval link for redirecting the user to complete payment

    private String stripePaymentIntentId;
}
