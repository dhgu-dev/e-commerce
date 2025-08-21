package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PaymentModel;
import com.loopers.domain.payment.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentModel, Long> {
    Optional<PaymentModel> findByTransactionKey(String transactionKey);

    List<PaymentModel> findAllByOrderId(String orderId);

    @Query("SELECT p FROM PaymentModel p WHERE p.status = :status AND p.createdAt <= :threshold")
    List<PaymentModel> findAllPendingOlderThan(
        @Param("status") TransactionStatus status,
        @Param("threshold") LocalDateTime threshold
    );
}
