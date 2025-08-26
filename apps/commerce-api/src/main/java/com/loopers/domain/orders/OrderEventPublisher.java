package com.loopers.domain.orders;

public interface OrderEventPublisher {
    void publish(OrderEvent.OrderCreatedEvent event);

    void publish(OrderEvent.OrderCancelledEvent event);
}
