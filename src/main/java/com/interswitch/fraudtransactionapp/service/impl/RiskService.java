package com.interswitch.fraudtransactionapp.service.impl;

import com.interswitch.fraudtransactionapp.dao.FraudDao;
import com.interswitch.fraudtransactionapp.model.IpRisk;
import com.interswitch.fraudtransactionapp.model.MerchantRisk;
import com.interswitch.fraudtransactionapp.model.RiskUpdateResult;
import com.interswitch.fraudtransactionapp.repository.IpRiskRepository;
import com.interswitch.fraudtransactionapp.repository.MerchantRiskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final FraudDao fraudDao;
    private final CacheManager cacheManager;
    private final IpRiskRepository ipRiskRepository;
    private final MerchantRiskRepository merchantRiskRepository;
    public RiskUpdateResult updateRisk(String ip, String merchantId, int delta) {

        RiskUpdateResult result = fraudDao.updateRiskScores(ip, merchantId, delta);

        Objects.requireNonNull(cacheManager.getCache("ipRisks")).put(ip, result.ipScore());
        Objects.requireNonNull(cacheManager.getCache("merchantRisks")).put(merchantId, result.merchantScore());

        return result;
    }

//    public int resolveIpRisk(String ip) {
//
//        Integer cached = getIpRisk(ip);
//        if (cached != null) return cached;
//
//        int dbValue = ipRiskRepository.findById(ip)
//                .map(IpRisk::getRiskScore)
//                .orElse(0);
//
//        updateIpCache(ip, dbValue);
//        return dbValue;
//    }
//
//    public int resolveMerchantRisk(String merchantId) {
//
//        Integer cached = getMerchantRisk(merchantId);
//        if (cached != null) return cached;
//
//        int dbValue = merchantRiskRepository.findById(merchantId)
//                .map(MerchantRisk::getRiskScore)
//                .orElse(0);
//
//        updateMerchantCache(merchantId, dbValue);
//        return dbValue;
//    }
//
//    private void updateIpCache(String ip, int score) {
//        Cache cache = cacheManager.getCache("ipRisks");
//        if (cache != null) {
//            cache.put(ip, score);
//        }
//    }
}