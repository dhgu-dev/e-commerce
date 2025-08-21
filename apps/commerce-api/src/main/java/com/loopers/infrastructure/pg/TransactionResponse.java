package com.loopers.infrastructure.pg;

import com.loopers.domain.payment.TransactionResult;
import com.loopers.domain.payment.TransactionStatus;

public record TransactionResponse(String transactionKey, TransactionStatusResponse status, String reason) {
    public static TransactionResult toResult(TransactionResponse response) {
        return new TransactionResult(
            response.transactionKey(),
            response.status() == TransactionStatusResponse.PENDING ? TransactionStatus.PENDING
                : response.status() == TransactionStatusResponse.SUCCESS ? TransactionStatus.SUCCESS
                : TransactionStatus.FAILED,
            response.reason()
        );
    }

    public enum TransactionStatusResponse {
        PENDING,
        SUCCESS,
        FAILED
    }
}
