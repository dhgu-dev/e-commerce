package com.loopers.infrastructure.pg;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.TransactionDetailResult;
import com.loopers.domain.payment.TransactionStatus;

import java.math.BigDecimal;

public record TransactionDetailResponse(
    String transactionKey,
    String orderId,
    CardTypeResponse cardType,
    String cardNo,
    BigDecimal amount,
    TransactionStatusResponse status,
    String reason
) {
    public static TransactionDetailResult toResult(TransactionDetailResponse response) {
        return new TransactionDetailResult(
            response.transactionKey(),
            OrderKeyEncoder.decode(response.orderId()).toString(),
            response.cardType().toCardType(),
            response.cardNo(),
            response.amount(),
            response.status().toTransactionStatus(),
            response.reason()
        );
    }

    public enum CardTypeResponse {
        SAMSUNG,
        KB,
        HYUNDAI;

        public CardType toCardType() {
            return switch (this) {
                case SAMSUNG -> CardType.SAMSUNG;
                case KB -> CardType.KB;
                case HYUNDAI -> CardType.HYUNDAI;
            };
        }
    }

    public enum TransactionStatusResponse {
        PENDING,
        SUCCESS,
        FAILED;

        public TransactionStatus toTransactionStatus() {
            return switch (this) {
                case PENDING -> TransactionStatus.PENDING;
                case SUCCESS -> TransactionStatus.SUCCESS;
                case FAILED -> TransactionStatus.FAILED;
            };
        }
    }
}
