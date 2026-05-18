package com.interswitch.fraudtransactionapp.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, FraudProfile> fraudProfileCache() {
        return Caffeine.newBuilder()
                .maximumSize(100_000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build();
    }


}