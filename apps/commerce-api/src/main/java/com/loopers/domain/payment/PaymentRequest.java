package com.loopers.domain.payment;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.orders.OrdersModel;
import lombok.Getter;

import java.math.BigDecimal;

public abstract class PaymentRequest {
    @Getter
    private PaymentType paymentType;

    @Getter
    private MemberModel member;

    @Getter
    private OrdersModel order;

    @Getter
    private BigDecimal amount;


    public PaymentRequest(PaymentType paymentType, MemberModel member, OrdersModel order, BigDecimal amount) {
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        if (member == null) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if (order == null) {
            throw new IllegalArgumentException("Order ID cannot be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }
        this.paymentType = paymentType;
        this.member = member;
        this.order = order;
        this.amount = amount;
    }
}
