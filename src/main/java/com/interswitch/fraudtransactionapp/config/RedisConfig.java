package com.interswitch.fraudtransactionapp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        try {
            factory.getConnection().ping();

            Map<String, RedisCacheConfiguration> configs = new HashMap<>();

            RedisCacheConfiguration baseConfig = RedisCacheConfiguration
                    .defaultCacheConfig()
                    .disableCachingNullValues();

            configs.put("blacklist", baseConfig.entryTtl(Duration.ofDays(3)));
            configs.put("risk", baseConfig.entryTtl(Duration.ofMinutes(30)));
            configs.put("velocity", baseConfig.entryTtl(Duration.ofMinutes(5)));
            configs.put("profile", baseConfig.entryTtl(Duration.ofHours(6)));
            return RedisCacheManager.builder(factory)
                    .cacheDefaults(baseConfig)
                    .build();

        } catch (Exception ex) {
            // Auto-create caches on demand
            ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager();
            manager.setAllowNullValues(true);
            return manager;
        }
    }

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }
}