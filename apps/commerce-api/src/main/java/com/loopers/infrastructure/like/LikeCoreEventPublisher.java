package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCoreEventPublisher implements LikeEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(LikeEvent.LikeMarkedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(LikeEvent.LikeUnmarkedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(LikeEvent.LikeChangedEvent event) {
        kafkaTemplate.send("catalog-events", event.productId().toString(), event);
    }
}
