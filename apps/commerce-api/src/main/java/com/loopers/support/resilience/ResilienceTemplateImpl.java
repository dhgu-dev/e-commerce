package com.loopers.support.resilience;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ResilienceTemplateImpl implements ResilienceTemplate {
    static Logger logger = LoggerFactory.getLogger(ResilienceTemplateImpl.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public <T> T circuitBreaker(String circuitBreakerName, Consumer<CircuitBreakerConfig.Builder> configCustomizer, Supplier<T> block, Function<CircuitBreakerException, T> fallback) {
        CircuitBreakerConfig config = circuitBreakerRegistry.getConfiguration(circuitBreakerName)
            .orElseGet(() -> {
                CircuitBreakerConfig.Builder builder = CircuitBreakerConfig.from(circuitBreakerRegistry.getDefaultConfig());
                configCustomizer.accept(builder);
                CircuitBreakerConfig builtConfig = builder.build();
                circuitBreakerRegistry.addConfiguration(circuitBreakerName, builtConfig);
                return builtConfig;
            });

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName, config);

        try {
            return circuitBreaker.executeCallable(block::get);
        } catch (Throwable e) {
            CircuitBreakerException.Reason reason;
            if (e instanceof CallNotPermittedException) {
                reason = CircuitBreakerUtils.determineFailureReason(circuitBreaker);
                switch (reason) {
                    case FAILURE -> logger.error("CircuitBreaker FAILURE", e);
                    case SLOW -> logger.warn("CircuitBreaker SLOW", e);
                }
            } else {
                logger.error("CircuitBreaker Exception", e);
                reason = CircuitBreakerException.Reason.FAILURE;
            }
            return fallback.apply(new CircuitBreakerException(e, reason));
        }
    }
}
