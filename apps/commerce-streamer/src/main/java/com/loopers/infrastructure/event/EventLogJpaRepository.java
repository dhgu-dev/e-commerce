package com.loopers.infrastructure.event;

import com.loopers.domain.event.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogJpaRepository extends JpaRepository<EventLog, String> {
}
