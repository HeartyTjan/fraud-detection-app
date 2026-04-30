package com.interswitch.fraudtransactionapp.health;


import lombok.RequiredArgsConstructor;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("fraudDatabase")
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        long startTime = System.currentTimeMillis();

        try {
            Integer txCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM transactions WHERE 1=0", Integer.class);

            Integer blacklistCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM blacklisted_card WHERE 1=0", Integer.class);

            long responseTime = System.currentTimeMillis() - startTime;

            return Health.up()
                    .withDetail("responseTimeMs", responseTime)
                    .withDetail("transactionsTable", "Accessible")
                    .withDetail("blacklistTable", "Accessible")
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("responseTimeMs", System.currentTimeMillis() - startTime)
                    .build();
        }
    }
}
