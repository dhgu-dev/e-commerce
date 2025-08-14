package com.loopers.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;

public interface VersioningStrategy {
    <T> String getVersion(Class<T> clazz);

    <T> String getVersion(TypeReference<T> type);
}
