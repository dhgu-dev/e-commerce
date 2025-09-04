package com.loopers.domain.orders;

public interface OrderEventPublisher {
    void publish(OrderEvent.OrderCreatedEvent event);

    void publish(OrderEvent.OrderProcessedEvent event);

    void publish(OrderEvent.OrderCanceledEvent event);

    void pusblish(OrderEvent.ProductSoldEvent event);
}
