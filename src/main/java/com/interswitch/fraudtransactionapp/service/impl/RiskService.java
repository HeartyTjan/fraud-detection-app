package com.interswitch.fraudtransactionapp.service.impl;


import com.interswitch.fraudtransactionapp.model.IpRisk;
import com.interswitch.fraudtransactionapp.model.MerchantRisk;
import com.interswitch.fraudtransactionapp.repository.IpRiskRepository;
import com.interswitch.fraudtransactionapp.repository.MerchantRiskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final IpRiskRepository ipRiskRepository;
    private final MerchantRiskRepository merchantRiskRepository;

    @Cacheable(value = "ipRisks", key = "#ip")
    public Integer getIpRisk(String ip) {
        return ipRiskRepository.findById(ip)
                .map(IpRisk::getRiskScore)
                .orElse(0);
    }

    @Cacheable(value = "merchantRisks", key = "#merchantId")
    public Integer getMerchantRisk(String merchantId) {
        return merchantRiskRepository.findById(merchantId)
                .map(MerchantRisk::getRiskScore)
                .orElse(0);
    }

    @CachePut(value = "ipRisks", key = "#ip")
    public Integer updateIpRisk(String ip, int newScore) {
        IpRisk ipRisk = ipRiskRepository.findById(ip).orElse(new IpRisk());
        ipRisk.setIpAddress(ip);
        ipRisk.setRiskScore(newScore);
        ipRiskRepository.save(ipRisk);
        return newScore;
    }

    @CachePut(value = "merchantRisks", key = "#merchantId")
    public Integer updateMerchantRisk(String merchantId, int newScore) {
        MerchantRisk merchantRisk = merchantRiskRepository.findById(merchantId)
                .orElse(new MerchantRisk());
        merchantRisk.setMerchantId(merchantId);
        merchantRisk.setRiskScore(newScore);
        merchantRiskRepository.save(merchantRisk);
        return newScore;
    }
}