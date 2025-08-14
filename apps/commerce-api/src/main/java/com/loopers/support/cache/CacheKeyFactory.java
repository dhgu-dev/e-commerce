package com.loopers.support.cache;

import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.StringJoiner;

public class CacheKeyFactory {

    public static String createKeyForPagedQuery(String resource, Map<String, Object> filters, Sort sort, long page, long size) {
        StringJoiner joiner = new StringJoiner(":");

        if (resource == null || resource.isEmpty()) {
            throw new IllegalArgumentException("Resource cannot be null or empty");
        }

        joiner.add("cache:" + resource);

        if (filters != null && !filters.isEmpty()) {
            filters.forEach((key, value) -> joiner.add(key + "=" + value));
        }

        if (sort != null && !sort.isEmpty()) {
            sort.stream().map((o) -> o.getProperty() + "_" + o.getDirection().name().toLowerCase())
                .forEach(sortBy -> joiner.add("sort=" + sortBy));
        }

        joiner.add("page=" + page);
        joiner.add("size=" + size);
        return joiner.toString();
    }
}
