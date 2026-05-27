package com.interswitch.fraudtransactionapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
public class AsyncFraudConfig {

    @Bean(name = "fraudRuleExecutor")
    public ExecutorService fraudRuleExecutor() {
        return new ThreadPoolExecutor(
                8,
                16,
                60L,
                java.util.concurrent.TimeUnit.SECONDS,
                new java.util.concurrent.LinkedBlockingQueue<>(500),

                r -> {
                    Thread t = new Thread(r, "fraud-rule-worker");
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
