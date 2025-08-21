package com.loopers.application.payment.dto;

import com.loopers.domain.payment.CardType;

import java.math.BigDecimal;

public record PaymentOrder(
    PaymentMethod paymentMethod,
    String userId,
    Long orderId,
    BigDecimal amount,
    CardTypeInfo cardType,
    String cardNo
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

    public enum PaymentMethod {
        CARD,
        POINTS
    }
}
