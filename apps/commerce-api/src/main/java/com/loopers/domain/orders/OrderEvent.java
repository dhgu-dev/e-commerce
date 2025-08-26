package com.loopers.domain.orders;

import java.util.List;

public class OrderEvent {

    public record OrderCreatedEvent(Long orderId, Long memberId, List<Long> productIds, Long couponId) {
    }

    public record OrderCancelledEvent(Long orderId, Long memberId, List<Long> productIds, Long couponId) {
    }
}
