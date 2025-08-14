package com.loopers.domain.common;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CacheManager<T> {
    void save(String baseKey, T value, Class<T> type, long timeout, TimeUnit unit);

    Optional<T> find(String baseKey, Class<T> type);

    void save(String baseKey, T value, TypeReference<T> type, long timeout, TimeUnit unit);

    Optional<T> find(String baseKey, TypeReference<T> type);

    void delete(String baseKey);
}
