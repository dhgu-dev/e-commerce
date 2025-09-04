package com.loopers.support;

public interface CacheManager<T> {
    void delete(String baseKey, Class<T> type);
}
