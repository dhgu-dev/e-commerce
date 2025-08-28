package com.loopers.domain.member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserActionLogger {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public <T> void logAction(Long userId, String action, T detail) {
        log.info("UserAction | userId={} | action={} | detail={}", userId, action, detail);
    }
}
