package com.loopers.domain.payment;

public class PaymentEvent {
    public record PaymentCompletedEvent(Long paymentId, String orderId, String transactionKey, Long amount) {
    }

    public record PaymentFailedEvent(Long paymentId, String orderId, String transactionKey, Long amount, String reason) {
    }
}
