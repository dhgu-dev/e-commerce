package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductEvent;
import com.loopers.domain.product.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCoreEventPublisher implements ProductEventPublisher {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(ProductEvent.StockAdjustedEvent event) {
        kafkaTemplate.send("catalog-events", event.productId().toString(), event);
    }

    @Override
    public void publish(ProductEvent.ProductDetailViewedEvent event) {
        kafkaTemplate.send("catalog-events", event.productId().toString(), event);
    }
}
