package com.loopers.infrastructure.event;

import com.loopers.domain.event.EventHandled;
import com.loopers.domain.event.EventHandledRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventHandledRepositoryImpl implements EventHandledRepository {

    private final EventHandledJpaRepository eventHandledJpaRepository;

    @Override
    public boolean exists(String eventId, String consumerName) {
        return eventHandledJpaRepository.existsByEventIdAndConsumerName(eventId, consumerName);
    }

    @Override
    public void save(EventHandled eventHandled) {
        eventHandledJpaRepository.save(eventHandled);
    }
}
