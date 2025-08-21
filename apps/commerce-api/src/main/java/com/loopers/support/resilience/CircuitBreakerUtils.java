package com.loopers.support.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

public class CircuitBreakerUtils {
    public static CircuitBreakerException.Reason determineFailureReason(CircuitBreaker circuitBreaker) {
        if (circuitBreaker.getMetrics().getFailureRate() >= circuitBreaker.getCircuitBreakerConfig().getFailureRateThreshold()) {
            return CircuitBreakerException.Reason.FAILURE;
        } else if (circuitBreaker.getMetrics().getSlowCallRate() >= circuitBreaker.getCircuitBreakerConfig().getSlowCallRateThreshold()) {
            return CircuitBreakerException.Reason.SLOW;
        } else {
            return CircuitBreakerException.Reason.FAILURE;
        }
    }
}
