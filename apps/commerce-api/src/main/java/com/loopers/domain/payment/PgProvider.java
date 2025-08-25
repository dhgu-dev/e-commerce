package com.loopers.domain.payment;

public interface PgProvider<T extends PaymentRequest> {
    TransactionResult requestTransaction(T request);

    TransactionDetailResult getTransactionDetail(String userId, String transactionKey);

    OrderResult getTransactionsByOrder(String userId, String orderId);
}
