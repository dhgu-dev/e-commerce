package com.loopers.support;

import com.loopers.config.redis.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisCacheManager<T> implements CacheManager<T> {

    private final RedisTemplate<String, String> redisMasterTemplate;
    private final VersioningStrategy versioningStrategy;

    public RedisCacheManager(
        @Qualifier(RedisConfig.REDIS_TEMPLATE_MASTER) RedisTemplate<String, String> redisMasterTemplate,
        VersioningStrategy versioningStrategy
    ) {
        this.redisMasterTemplate = redisMasterTemplate;
        this.versioningStrategy = versioningStrategy;
    }

    @Override
    public void delete(String baseKey, Class<T> type) {
        String key = buildVersionedKey(baseKey, type);
        log.info("delete key: {}", key);
        redisMasterTemplate.delete(key);
    }

    private String buildVersionedKey(String baseKey, Class<T> type) {
        String version = versioningStrategy.getVersion(type);
        return String.format("%s::%s", baseKey, version);
    }
}
