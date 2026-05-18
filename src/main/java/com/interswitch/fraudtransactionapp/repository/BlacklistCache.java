package com.interswitch.fraudtransactionapp.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BlacklistCache {

    private final BlacklistedCardRepository cardRepository;
    private final BlackListedIpRepository ipRepository;
    private  final BlackListedMerchantRepository merchantRepository;
    private final ProviderBlacklistedIPRepository providerBlacklistedIPRepository;
    private final StringRedisTemplate redisTemplate;



    @PostConstruct
    public void preload() {

        cardRepository.findAll()
                .forEach(card ->
                        redisTemplate.opsForValue()
                                .set("bl:card:" + card.getCardNo(), "1"));

        ipRepository.findAll()
                .forEach(ip ->
                        redisTemplate.opsForValue()
                                .set("bl:ip:" + ip.getIp(), "1"));

        merchantRepository.findAll()
                .forEach(merchant ->
                        redisTemplate.opsForValue()
                                .set("bl:merchant:" + merchant.getMerchantId(), "1"));

        providerBlacklistedIPRepository.findAll()
                .forEach(ip -> redisTemplate.opsForValue().set("bl:provider-ip:" + ip.getIp(), "1"));
    }

    public void addCard(String cardNo) {
        redisTemplate.opsForValue()
                .set("bl:card:" + cardNo, "1");
    }

    public void addIp(String ip) {
        redisTemplate.opsForValue()
                .set("bl:ip:" + ip, "1");
    }

    public void addMerchant(String merchantId) {
        redisTemplate.opsForValue()
                .set("bl:merchant:" + merchantId, "1");
    }

    public boolean isCardBlacklisted(String cardNo) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("bl:card:" + cardNo)
        );
    }

    public boolean isIpBlacklisted(String ip) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("bl:ip:" + ip)
        );
    }

    public boolean isMerchantBlacklisted(String merchantId) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("bl:merchant:" + merchantId)
        );
    }

    public boolean isProviderIpBlacklisted(String ip) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("bl:provider-ip:" + ip)
        );
    }


}