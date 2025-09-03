package com.loopers.domain.product;

import java.time.ZonedDateTime;

public class ProductEvent {
    public record StockAdjustedEvent(String eventId, Long productId, Long stock, ZonedDateTime updatedAt, String eventName,
                                     Long memberId) {
    }

    public record ProductDetailViewedEvent(String eventId, Long productId, ZonedDateTime viewedAt, String eventName) {
    }
}
