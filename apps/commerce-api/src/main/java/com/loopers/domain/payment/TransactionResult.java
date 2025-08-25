package com.loopers.domain.payment;

public record TransactionResult(String transactionKey, TransactionStatus status, String reason) {
}
