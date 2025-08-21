package com.loopers.infrastructure.pg;

import com.loopers.domain.payment.CardType;

public record TransactionRequest(
    String orderId,
    CardTypeDto cardType,
    String cardNo,
    Long amount,
    String callbackUrl
) {
    public enum CardTypeDto {
        SAMSUNG,
        KB,
        HYUNDAI;

        public static CardTypeDto from(CardType cardType) {
            return switch (cardType) {
                case SAMSUNG -> SAMSUNG;
                case KB -> KB;
                case HYUNDAI -> HYUNDAI;
                default -> throw new IllegalArgumentException("Unknown card type: " + cardType);
            };
        }
    }
}
