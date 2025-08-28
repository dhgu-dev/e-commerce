package com.loopers.domain.like;

public class LikeEvent {
    public record LikeMarkedEvent(Long memberId, Long productId) {
    }

    public record LikeUnmarkedEvent(Long memberId, Long productId) {
    }
}
