package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCoreEventPublisher implements LikeEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(LikeEvent.LikeMarkedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(LikeEvent.LikeUnmarkedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
