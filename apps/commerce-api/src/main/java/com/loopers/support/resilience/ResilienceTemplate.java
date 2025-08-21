package com.loopers.support.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ResilienceTemplate {
    <T> T circuitBreaker(
        String circuitBreakerName,
        Consumer<CircuitBreakerConfig.Builder> configCustomizer,
        Supplier<T> block,
        Function<CircuitBreakerException, T> fallback
    );
}
