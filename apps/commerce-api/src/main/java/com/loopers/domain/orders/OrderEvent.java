package com.loopers.domain.orders;

import java.util.List;

public class OrderEvent {

    public record OrderCreatedEvent(Long orderId, Long memberId, List<Long> productIds, Long couponId) {
    }

    public record OrderProcessedEvent(Long orderId, Long memberId, List<Long> productIds, Long couponId) {
    }

    public record OrderCanceledEvent(Long orderId, Long memberId, List<Long> productIds, Long couponId) {
    }
}
