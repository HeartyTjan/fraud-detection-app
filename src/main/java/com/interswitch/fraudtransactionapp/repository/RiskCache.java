//package com.interswitch.fraudtransactionapp.repository;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class RiskCache {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    private String ipKey(String ip) {
//        return "ip_risk:" + ip;
//    }
//
//    private String merchantKey(String merchantId) {
//        return "merchant_risk:" + merchantId;
//    }
//
//    public Integer getIpRisk(String ip) {
//        return (Integer) redisTemplate.opsForValue().get(ipKey(ip));
//    }
//
//    public void setIpRisk(String ip, int score) {
//        redisTemplate.opsForValue().set(ipKey(ip), score);
//    }
//
//    public Integer getMerchantRisk(String merchantId) {
//        return (Integer) redisTemplate.opsForValue().get(merchantKey(merchantId));
//    }
//
//    public void setMerchantRisk(String merchantId, int score) {
//        redisTemplate.opsForValue().set(merchantKey(merchantId), score);
//    }
//}