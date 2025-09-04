package com.loopers.infrastructure.event;

import com.loopers.domain.event.EventLog;
import com.loopers.domain.event.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventLogRepositoryImpl implements EventLogRepository {

    private final EventLogJpaRepository eventLogJpaRepository;

    @Override
    public void save(EventLog eventLog) {
        eventLogJpaRepository.save(eventLog);
    }
}
