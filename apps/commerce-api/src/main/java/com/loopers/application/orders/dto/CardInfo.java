package com.loopers.application.orders.dto;

import com.loopers.domain.payment.CardType;

public record CardInfo(
    CardTypeInfo cardTypeInfo,
    String cardNumber
) {
    public enum CardTypeInfo {
        SAMSUNG,
        KB,
        HYUNDAI;

        public static CardTypeInfo from(CardType cardType) {
            return switch (cardType) {
                case SAMSUNG -> SAMSUNG;
                case KB -> KB;
                case HYUNDAI -> HYUNDAI;
                default -> throw new IllegalArgumentException("Unknown card type: " + cardType);
            };
        }

        public CardType toCardType() {
            return switch (this) {
                case SAMSUNG -> CardType.SAMSUNG;
                case KB -> CardType.KB;
                case HYUNDAI -> CardType.HYUNDAI;
            };
        }
    }
}
