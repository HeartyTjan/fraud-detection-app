package com.interswitch.fraudtransactionapp.health;


import com.interswitch.fraudtransactionapp.config.RateLimiter;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("rateLimiterHealth")
@RequiredArgsConstructor
public class RateLimiterHealthIndicator implements HealthIndicator {

    private final RateLimiter rateLimiter;

    @Override
    public Health health() {
        boolean allowed = rateLimiter.isAllowedByIp("HEALTH_CHECK_127.0.0.1");

        if (allowed) {
            return Health.up()
                    .withDetail("status", "Rate limiter operational")
                    .withDetail("type", "In-memory sliding window")
                    .withDetail("limit", "5 requests/minute per IP")
                    .build();
        } else {
            return Health.down()
                    .withDetail("status", "Rate limit threshold reached for health IP")
                    .withDetail("type", "In-memory sliding window")
                    .withDetail("limit", "5 requests/minute per IP")
                    .build();
        }
    }
}