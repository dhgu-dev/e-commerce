package com.loopers.support;

public interface VersioningStrategy {
    <T> String getVersion(Class<T> type);
}
