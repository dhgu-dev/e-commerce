package com.loopers.domain.orders;

public interface OrderEventPublisher {
    void publish(OrderEvent.OrderCreatedEvent event);
}
