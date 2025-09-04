package com.loopers.infrastructure.orders;

import com.loopers.domain.orders.OrderEvent;
import com.loopers.domain.orders.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoreEventPublisher implements OrderEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(OrderEvent.OrderCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(OrderEvent.OrderProcessedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(OrderEvent.OrderCanceledEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void pusblish(OrderEvent.ProductSoldEvent event) {
        kafkaTemplate.send("order-events", event.orderId().toString(), event);
    }

}
