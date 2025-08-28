package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCoreEventPublisher implements PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(PaymentEvent.PaymentCompletedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentEvent.PaymentFailedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
