//package com.interswitch.fraudtransactionapp.client;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//
//@Component
//@RequiredArgsConstructor
//public class StartupRunner {
//
//    private final SikkariProvider sikkariProvider;
//
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void runAfterStartup() {
//        sikkariProvider.getBlacklistedIpAndSaveToDB();
//    }
//
//    @Scheduled(fixedDelay = 86400000, initialDelay = 86400000)
//    public void scheduledFetch() {
//        sikkariProvider.getBlacklistedIpAndSaveToDB();
//    }
//}