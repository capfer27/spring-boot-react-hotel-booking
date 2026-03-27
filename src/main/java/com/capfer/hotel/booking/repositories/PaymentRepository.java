package com.capfer.hotel.booking.repositories;

import com.capfer.hotel.booking.entities.PaymentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    // Returns Optional to handle cases where the payment doesn't exist yet
    Optional<PaymentEntity> findByStripePaymentIntentId(String stripePaymentIntentId);

    // Useful for checking existence without loading the whole entity
    boolean existsByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Database-Level Locking (Pessimistic)
     * If you are updating a status (e.g., from PENDING to COMPLETED) and want to ensure
     * no two threads modify the same row simultaneously, then use a Pessimistic Lock.
     * @param id
     * @return Optional<PaymentEntity>
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentEntity p WHERE p.stripePaymentIntentId = :id")
    Optional<PaymentEntity> findByStripePaymentIntentIdWithLock(@Param("id") String id);
}
