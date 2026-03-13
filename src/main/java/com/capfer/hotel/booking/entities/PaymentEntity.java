package com.capfer.hotel.booking.entities;

import com.capfer.hotel.booking.enums.PaymentGateway;
import com.capfer.hotel.booking.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_gateway")
    private PaymentGateway paymentGateway;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    // This can be used to link the payment to a booking, but we won't use a foreign key to avoid tight coupling.
    @Column(name = "booking_reference", nullable = false)
    private String bookingReference;

    // Optional field to store failure reason if payment fails
    @Column(name = "failure_reason")
    private String failureReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEntity that = (PaymentEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(transactionId, that.transactionId) && Objects.equals(amount, that.amount) && paymentGateway == that.paymentGateway && Objects.equals(paymentDate, that.paymentDate) && paymentStatus == that.paymentStatus && Objects.equals(bookingReference, that.bookingReference) && Objects.equals(failureReason, that.failureReason) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionId, amount, paymentGateway, paymentDate, paymentStatus, bookingReference, failureReason, user);
    }

    @Override
    public String toString() {
        return "PaymentEntity{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", paymentGateway=" + paymentGateway +
                ", paymentDate=" + paymentDate +
                ", paymentStatus=" + paymentStatus +
                ", bookingReference='" + bookingReference + '\'' +
                ", failureReason='" + failureReason + '\'' +
//                ", user.email=" + user.getEmail() +
                '}';
    }
}
