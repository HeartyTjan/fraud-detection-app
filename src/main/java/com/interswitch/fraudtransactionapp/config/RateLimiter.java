package com.interswitch.fraudtransactionapp.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


@Component
public class RateLimiter {

    private static final int MAX_REQUESTS_PER_MIN = 5;
    private static final long WINDOW_MS = 60_000L; // 1 minute

    private final Map<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    public boolean isAllowedByIp(String ip) {
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_MS;

        Deque<Long> timestamps = requestLog.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());

        while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < MAX_REQUESTS_PER_MIN) {
            timestamps.addLast(now);
            return true;
        }
        return false;

    }

    /**
     * Cleanup stale entries every 5 minutes to prevent memory leaks.
     */
    @Scheduled(fixedRate = 300_000)
    public void cleanup() {
        long windowStart = System.currentTimeMillis() - WINDOW_MS;

        requestLog.entrySet().removeIf(entry -> {
            Deque<Long> timestamps = entry.getValue();
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }
            return timestamps.isEmpty();
        });
    }
}
