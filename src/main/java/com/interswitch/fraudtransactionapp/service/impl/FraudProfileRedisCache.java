package com.interswitch.fraudtransactionapp.service.impl;

import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FraudProfileRedisCache {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration TTL = Duration.ofSeconds(30);

    public FraudProfile get(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) return null;

            return objectMapper.readValue(value, FraudProfile.class);

        } catch (Exception e) {
            return null;
        }
    }

    public void put(String key, FraudProfile profile) {
        try {
            String json = objectMapper.writeValueAsString(profile);
            redisTemplate.opsForValue().set(key, json, TTL);

        } catch (Exception ignored) {}
    }

    public void printValue(String key) {
        String value = redisTemplate.opsForValue().get(key);
        System.out.println("Stored Value: " + value);
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }
}
