package com.loopers.domain.orders;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderEvent {

    public record OrderCreatedEvent(Long orderId, Long memberId, List<Long> productIds, Long couponId) {
    }

    public record OrderProcessedEvent(Long orderId, Long memberId, List<Long> productIds, Long couponId) {
    }

    public record OrderCanceledEvent(Long orderId, Long memberId, List<Long> productIds, Long couponId) {
    }

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
}
