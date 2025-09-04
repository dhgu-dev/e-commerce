package com.loopers.domain.event;

public interface EventHandledRepository {
    boolean exists(String eventId, String consumerName);

    void save(EventHandled eventHandled);
}
