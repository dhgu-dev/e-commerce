package com.loopers.domain.payment;

public interface PgProvider<T extends PaymentRequest> {
    TransactionResult requestTransaction(T request);
}
