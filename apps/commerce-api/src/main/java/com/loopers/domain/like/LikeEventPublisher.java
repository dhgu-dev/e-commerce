package com.loopers.domain.like;

public interface LikeEventPublisher {
    void publish(LikeEvent.LikeMarkedEvent event);

    void publish(LikeEvent.LikeUnmarkedEvent event);

    void publish(LikeEvent.LikeChangedEvent event);
}
