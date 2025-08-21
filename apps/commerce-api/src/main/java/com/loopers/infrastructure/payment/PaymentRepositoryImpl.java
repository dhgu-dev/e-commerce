package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PaymentModel;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.payment.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    @Transactional
    public PaymentModel save(PaymentModel paymentModel) {
        return paymentJpaRepository.save(paymentModel);
    }

    @Override
    public Optional<PaymentModel> findByTxKey(String transactionKey) {
        return paymentJpaRepository.findByTransactionKey(transactionKey);
    }

    @Override
    public List<PaymentModel> findAllByOrderId(String orderId) {
        return paymentJpaRepository.findAllByOrderId(orderId);
    }

    @Override
    public List<PaymentModel> findAllPendingOlderThan(TransactionStatus status, LocalDateTime threshold) {
        return paymentJpaRepository.findAllPendingOlderThan(status, threshold);
    }
}
