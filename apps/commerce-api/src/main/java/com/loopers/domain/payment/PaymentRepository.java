package com.loopers.domain.payment;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    PaymentModel save(PaymentModel payment);

    Optional<PaymentModel> findByTxKey(String transactionKey);

    List<PaymentModel> findAllByOrderId(String orderId);

    List<PaymentModel> findAllPendingOlderThan(TransactionStatus status, ZonedDateTime threshold);
}
