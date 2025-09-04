package com.loopers.domain.event;

import java.time.ZonedDateTime;

public class EventSchema {
    public record ProductSoldEvent(
        String eventId,
        Long orderId,
        Long productId,
        Long quantity,
        ZonedDateTime createdAt,
        String eventName,
        Long memberId
    ) {
    }

    public record StockAdjustedEvent(String eventId, Long productId, Long stock, ZonedDateTime createdAt, String eventName,
                                     Long memberId) {
    }

    public record ProductDetailViewedEvent(String eventId, Long productId, ZonedDateTime viewedAt, String eventName) {
    }

    public record LikeChangedEvent(String eventId, Long memberId, Long productId, ZonedDateTime createdAt, String eventName) {
    }
}
