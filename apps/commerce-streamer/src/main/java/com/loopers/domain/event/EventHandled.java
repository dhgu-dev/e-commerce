package com.loopers.domain.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "event_handled")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(EventHandledId.class)
public class EventHandled {
    @Id
    @Column(name = "event_id")
    @Getter
    private String eventId;

    @Id
    @Column(name = "consumer_name", nullable = false)
    private String consumerName;

    @Column(name = "handled_at", nullable = false)
    @Getter
    private ZonedDateTime handledAt;

    public EventHandled(String eventId, String consumerName) {
        this.eventId = eventId;
        this.consumerName = consumerName;
        this.handledAt = ZonedDateTime.now();
    }
}
