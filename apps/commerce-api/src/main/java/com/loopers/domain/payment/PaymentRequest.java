package com.loopers.domain.payment;

import lombok.Getter;

import java.math.BigDecimal;

public abstract class PaymentRequest {
    @Getter
    private PaymentType paymentType;

    @Getter
    private String memberId;

    @Getter
    private String orderId;

    @Getter
    private BigDecimal amount;


    public PaymentRequest(PaymentType paymentType, String memberId, String orderId, BigDecimal amount) {
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }
        this.paymentType = paymentType;
        this.memberId = memberId;
        this.orderId = orderId;
        this.amount = amount;
    }
}
