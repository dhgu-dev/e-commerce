package com.loopers.domain.event;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class EventHandledId implements Serializable {
    private String eventId;
    private String consumerName;
}
