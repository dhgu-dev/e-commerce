package com.loopers.domain.payment;

import lombok.Getter;

import java.math.BigDecimal;

public class CardPaymentRequest extends PaymentRequest {
    @Getter
    private final CardType cardType;

    @Getter
    private final String cardNo;

    public CardPaymentRequest(
        String memberId,
        String orderId,
        BigDecimal amount,
        CardType cardType,
        String cardNo

    ) {
        super(PaymentType.CARD, memberId, orderId, amount);
        this.cardType = cardType;
        this.cardNo = cardNo;
    }
}
