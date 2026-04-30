package com.interswitch.fraudtransactionapp.health;


import com.interswitch.fraudtransactionapp.repository.BlacklistCache;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("fraudService")
@RequiredArgsConstructor
public class FraudServiceHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private final BlacklistCache blacklistCache;

    @Override
    public Health health() {
        try {
            Integer dbResult = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            boolean dbHealthy = dbResult != null && dbResult == 1;

            boolean cacheHealthy = checkCacheHealth();

            if (dbHealthy && cacheHealthy) {
                return Health.up()
                        .withDetail("database", "Connected")
                        .withDetail("blacklistCache", "Operational")
                        .withDetail("status", "Fraud detection service is healthy")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", dbHealthy ? "Connected" : "Disconnected")
                        .withDetail("blacklistCache", cacheHealthy ? "Operational" : "Degraded")
                        .build();
            }

        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private boolean checkCacheHealth() {
        try {
            blacklistCache.isCardBlacklisted("HEALTH_CHECK_DUMMY");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
