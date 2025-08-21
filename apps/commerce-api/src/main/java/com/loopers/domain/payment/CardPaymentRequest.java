package com.loopers.domain.payment;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.orders.OrdersModel;
import lombok.Getter;

import java.math.BigDecimal;

public class CardPaymentRequest extends PaymentRequest {
    @Getter
    private final CardType cardType;

    @Getter
    private final String cardNo;

    public CardPaymentRequest(
        MemberModel member,
        OrdersModel order,
        BigDecimal amount,
        CardType cardType,
        String cardNo

    ) {
        super(PaymentType.CARD, member, order, amount);
        this.cardType = cardType;
        this.cardNo = cardNo;
    }
}
