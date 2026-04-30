package com.interswitch.fraudtransactionapp.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiter();
    }

    @Test
    void shouldAllowFirstFiveRequests() {
        String ip = "192.168.1.1";

        for (int count = 0; count < 5; count++) {
            assertThat(rateLimiter.isAllowedByIp(ip))
                    .as("Request %d should be allowed", count + 1)
                    .isTrue();
        }
    }

    @Test
    void shouldBlockSixthRequest() {
        String ip = "192.168.1.2";

        for (int i = 0; i < 5; i++) {
            rateLimiter.isAllowedByIp(ip);
        }

        assertThat(rateLimiter.isAllowedByIp(ip)).isFalse();
    }

    @Test
    void shouldTrackDifferentIpsSeparately() {
        String ip1 = "10.0.0.1";
        String ip2 = "10.0.0.2";

        for (int i = 0; i < 5; i++) {
            rateLimiter.isAllowedByIp(ip1);
        }

        assertThat(rateLimiter.isAllowedByIp(ip2)).isTrue();

        assertThat(rateLimiter.isAllowedByIp(ip1)).isFalse();
    }

    @Test
    void shouldBlockMultipleRequestsAfterLimitReached() {
        String ip = "192.168.1.3";

        for (int i = 0; i < 5; i++) {
            rateLimiter.isAllowedByIp(ip);
        }

        for (int i = 0; i < 3; i++) {
            assertThat(rateLimiter.isAllowedByIp(ip))
                    .as("Request %d after limit should be blocked", i + 1)
                    .isFalse();
        }
    }
}
