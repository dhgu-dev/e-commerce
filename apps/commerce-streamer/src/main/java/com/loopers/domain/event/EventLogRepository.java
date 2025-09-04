package com.loopers.domain.event;

public interface EventLogRepository {
    void save(EventLog eventLog);
}
