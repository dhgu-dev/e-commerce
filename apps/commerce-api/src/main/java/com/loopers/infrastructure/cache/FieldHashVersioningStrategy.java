package com.loopers.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

@Component
public class FieldHashVersioningStrategy implements VersioningStrategy {
    @Override
    public <T> String getVersion(Class<T> clazz) {
        StringBuilder sb = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            sb.append(field.getName()).append(":").append(field.getType().getName()).append(";");
        }
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public <T> String getVersion(TypeReference<T> type) {
        StringBuilder sb = new StringBuilder();
        for (Field field : type.getClass().getDeclaredFields()) {
            sb.append(field.getName()).append(":").append(field.getType().getName()).append(";");
        }
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
