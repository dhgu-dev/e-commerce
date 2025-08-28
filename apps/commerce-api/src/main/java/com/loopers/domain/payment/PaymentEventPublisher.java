package com.loopers.domain.payment;

public interface PaymentEventPublisher {
    void publish(PaymentEvent.PaymentCompletedEvent event);

    void publish(PaymentEvent.PaymentFailedEvent event);
}
