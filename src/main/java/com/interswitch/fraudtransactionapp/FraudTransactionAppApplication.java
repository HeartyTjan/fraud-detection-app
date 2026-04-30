package com.interswitch.fraudtransactionapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
public class FraudTransactionAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudTransactionAppApplication.class, args);
    }

}
