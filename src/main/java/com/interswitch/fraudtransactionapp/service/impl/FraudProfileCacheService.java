package com.interswitch.fraudtransactionapp.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.interswitch.fraudtransactionapp.config.FraudKeyBuilder;
import com.interswitch.fraudtransactionapp.dao.FraudDao;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudProfileCacheService {

    private final Cache<String, FraudProfile> localCache;
    private final FraudProfileRedisCache redisCache;
    private final FraudDao fraudDao;
    private final FraudKeyBuilder keyBuilder;

    public FraudProfile get(TransactionRequest request) {

        String key = keyBuilder.build(request);

        System.out.println("REDIS KEY" + key);

        FraudProfile local = localCache.getIfPresent(key);
        if (local != null) return local;

        FraudProfile redis = redisCache.get(key);
        redisCache.printValue(key);

        if (redis != null) {
            localCache.put(key, redis);
            return redis;
        }

        FraudProfile profile = fraudDao.loadFraudProfile(
                request.getCardNo(),
                request.getIpAddress(),
                request.getMerchantId(),
                request.getTransactionTime()
        );

        redisCache.put(key, profile);
        localCache.put(key, profile);

        return profile;
    }

    public void invalidate(String key) {
        localCache.invalidate(key);
        redisCache.evict(key);
    }
}