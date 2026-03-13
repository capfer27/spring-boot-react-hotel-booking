package com.capfer.hotel.booking.entities;

import com.capfer.hotel.booking.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @NotBlank(message = "Recipient is required")
    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String body;

    // Optional field to link notification to a booking
    @Column(name = "booking_reference", nullable = false)
    private String bookingReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id) && Objects.equals(subject, that.subject) && Objects.equals(recipient, that.recipient) && Objects.equals(body, that.body) && Objects.equals(bookingReference, that.bookingReference) && notificationType == that.notificationType && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subject, recipient, body, bookingReference, notificationType, createdAt);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", recipient='" + recipient + '\'' +
                ", body='" + body + '\'' +
                ", bookingReference='" + bookingReference + '\'' +
                ", notificationType=" + notificationType +
                ", createdAt=" + createdAt +
                '}';
    }
}
