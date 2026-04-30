package com.interswitch.fraudtransactionapp.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;


@Component
@RequiredArgsConstructor
public class BlacklistCache {

    private final BlacklistedCardRepository cardRepository;
    private final BlackListedIpRepository ipRepository;
    private final ProviderBlacklistedIPRepository providerBlacklistedIPRepository;


    @PostConstruct
    public void preloadProviderIps() {
        providerBlacklistedIPRepository.findAll()
                .forEach(ip -> addProviderIpToCache(ip.getIp()));
    }
    @Cacheable(value = "blacklistedCards", key = "#cardNo")
    public boolean isCardBlacklisted(String cardNo) {
        return cardRepository.existsByCardNo(cardNo);
    }

    @CachePut(value = "blacklistedCards", key = "#cardNo")
    public boolean addCardToCache(String cardNo) {
        return true;
    }

    @CacheEvict(value = "blacklistedCards", key = "#cardNo")
    public void removeCardFromCache(String cardNo) {
    }

    @Cacheable(value = "blacklistedIps", key = "#ip")
    public boolean isIpBlacklisted(String ip) {
        return ipRepository.existsByIp(ip);
    }

    @CachePut(value = "blacklistedIps", key = "#ip")
    public boolean addIpToCache(String ip) {
        return true;
    }

    @CacheEvict(value = "blacklistedIps", key = "#ip")
    public void removeIpFromCache(String ip) {
    }

    @Cacheable(value = "providerBlacklistedIps", key = "#ip")
    public boolean isProviderIpBlacklisted(String ip) {
        return providerBlacklistedIPRepository.existsById(ip);
    }

    @CachePut(value = "providerBlacklistedIps", key = "#ip")
    public boolean addProviderIpToCache(String ip) {
        return true;
    }

    @CacheEvict(value = "providerBlacklistedIps", key = "#ip")
    public void removeProviderIpFromCache(String ip) {
    }

    private final BlackListedMerchantRepository merchantRepository;

    @Cacheable(value = "blacklistedMerchants", key = "#merchantId")
    public boolean isMerchantBlacklisted(String merchantId) {
        return merchantRepository.existsByMerchantId(merchantId);
    }

    @CachePut(value = "blacklistedMerchants", key = "#merchantId")
    public boolean addMerchantToCache(String merchantId) {
        return true;
    }

    @CacheEvict(value = "blacklistedMerchants", key = "#merchantId")
    public void removeMerchantFromCache(String merchantId) {
    }

}