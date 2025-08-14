package com.loopers.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.config.redis.RedisConfig;
import com.loopers.domain.common.CacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisCacheManager<T> implements CacheManager<T> {

    private final RedisTemplate<String, String> redisQueryTemplate;
    private final RedisTemplate<String, String> redisMasterTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheManager(
        RedisTemplate<String, String> redisQueryTemplate,
        @Qualifier(RedisConfig.REDIS_TEMPLATE_MASTER) RedisTemplate<String, String> redisMasterTemplate,
        ObjectMapper objectMapper
    ) {
        this.redisQueryTemplate = redisQueryTemplate;
        this.redisMasterTemplate = redisMasterTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(String baseKey, T value, Class<T> type, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            log.info("save key: {} value: {}", baseKey, json);
            redisMasterTemplate.opsForValue().set(baseKey, json, timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException("Redis 저장 중 오류", e);
        }
    }

    @Override
    public Optional<T> find(String baseKey, Class<T> type) {
        try {
            String json = redisQueryTemplate.opsForValue().get(baseKey);
            log.info("find key: {} value: {}", baseKey, json);
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, type));
        } catch (Exception e) {
            log.error("Redis 검색 중 오류: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving from Redis", e);
        }
    }

    @Override
    public void save(String baseKey, T value, TypeReference<T> type, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            log.info("save key: {} value: {}", baseKey, json);
            redisMasterTemplate.opsForValue().set(baseKey, json, timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException("Redis 저장 중 오류", e);
        }
    }

    @Override
    public Optional<T> find(String baseKey, TypeReference<T> type) {
        try {
            String json = redisQueryTemplate.opsForValue().get(baseKey);
            log.info("find key: {} value: {}", baseKey, json);
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, type));
        } catch (Exception e) {
            log.error("Redis 검색 중 오류: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving from Redis", e);
        }
    }

    @Override
    public void delete(String key) {
        redisMasterTemplate.delete(key);
    }
}
