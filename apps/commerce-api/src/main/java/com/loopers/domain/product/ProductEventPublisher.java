package com.loopers.domain.product;

public interface ProductEventPublisher {
    void publish(ProductEvent.StockAdjustedEvent event);

    void publish(ProductEvent.ProductDetailViewedEvent event);
}
