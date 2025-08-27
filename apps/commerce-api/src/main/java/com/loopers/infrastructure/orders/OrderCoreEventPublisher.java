package com.loopers.infrastructure.orders;

import com.loopers.domain.orders.OrderEvent;
import com.loopers.domain.orders.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoreEventPublisher implements OrderEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(OrderEvent.OrderCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
}
