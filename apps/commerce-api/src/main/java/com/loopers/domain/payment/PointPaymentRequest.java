package com.loopers.domain.payment;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.orders.OrdersModel;

import java.math.BigDecimal;

public class PointPaymentRequest extends PaymentRequest {
    public PointPaymentRequest(
        MemberModel member,
        OrdersModel order,
        BigDecimal amount
    ) {
        super(PaymentType.POINTS, member, order, amount);
    }
}
