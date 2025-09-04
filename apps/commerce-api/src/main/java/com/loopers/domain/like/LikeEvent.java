package com.loopers.domain.like;

import java.time.ZonedDateTime;

public class LikeEvent {
    public record LikeMarkedEvent(Long memberId, Long productId) {
    }

    public record LikeUnmarkedEvent(Long memberId, Long productId) {
    }

    public record LikeChangedEvent(String eventId, Long memberId, Long productId, ZonedDateTime createdAt, String eventName) {
    }
}
