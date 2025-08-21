package com.loopers.application.payment.dto;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.TransactionStatus;

public record SyncPaymentCommand(
    String transactionKey,
    String orderId,
    CardTypeInfo cardType,
    String cardNo,
    Long amount,
    TransactionStatusInfo status,
    String reason
) {
    public enum CardTypeInfo {
        SAMSUNG,
        KB,
        HYUNDAI;

        public CardType toDomain() {
            return switch (this) {
                case SAMSUNG -> CardType.SAMSUNG;
                case KB -> CardType.KB;
                case HYUNDAI -> CardType.HYUNDAI;
            };
        }
    }

    public enum TransactionStatusInfo {
        PENDING,
        SUCCESS,
        FAILED;

        public TransactionStatus toDomain() {
            return switch (this) {
                case PENDING -> TransactionStatus.PENDING;
                case SUCCESS -> TransactionStatus.SUCCESS;
                case FAILED -> TransactionStatus.FAILED;
            };
        }
    }
}
