package com.loopers.support.resilience;

public record CircuitBreakerException(Throwable e, Reason reason) {
    public enum Reason {
        FAILURE,
        SLOW
    }
}
