package com.loopers.domain.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "event_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventLog {
    @Id
    @Column(name = "event_id")
    @Getter
    private String eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    public EventLog(String eventId, String eventName, Long memberId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.memberId = memberId;
        this.createdAt = ZonedDateTime.now();
    }
}
