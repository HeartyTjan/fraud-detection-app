package com.interswitch.fraudtransactionapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncFraudConfig {

    @Bean(name = "fraudRuleExecutor")
    public ExecutorService fraudRuleExecutor() {
        return Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                r -> {
                    Thread t = new Thread(r, "fraud-rule-worker");
                    t.setDaemon(true);
                    return t;
                }
        );
    }
}
