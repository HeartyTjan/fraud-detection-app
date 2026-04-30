package com.interswitch.fraudtransactionapp.service.impl;

import com.interswitch.fraudtransactionapp.config.TrackExecution;
import com.interswitch.fraudtransactionapp.dao.BlackListedIpDao;
import com.interswitch.fraudtransactionapp.dao.BlackListedMerchantDao;
import com.interswitch.fraudtransactionapp.dao.TransactionJdbcDao;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.FraudEngine;
import com.interswitch.fraudtransactionapp.model.*;
import com.interswitch.fraudtransactionapp.repository.*;
import com.interswitch.fraudtransactionapp.service.FraudService;
import com.interswitch.fraudtransactionapp.util.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@TrackExecution
public class FraudServiceImpl implements FraudService {

    private final FraudEngine fraudEngine;
    private final IpRiskRepository ipRiskRepository;
    private final MerchantRiskRepository merchantRiskRepository;
    private final BlacklistedCardRepository blacklistedCardRepository;
//    private final BlackListedIpRepository blacklistedIpRepository;
//    private final BlackListedMerchantRepository blacklistedMerchantRepository;
    private final BlackListedMerchantDao blacklistedMerchantDao;
    private final BlackListedIpDao blacklistedIpDao;
    private final RiskService riskService;
    private final TransactionJdbcDao transactionJdbcDao;
//    private final TransactionRepository transactionRepository;


    @Transactional
    @Override
    public FraudDecision process(TransactionRequest request) {

        FraudDecision decision = fraudEngine.evaluate(request);

        handlePostDecision(request, decision);
        Transactions transaction = TransactionMapper.toEntity(request, decision);
        transactionJdbcDao.save(transaction);

        return decision;
    }

    private void handlePostDecision(TransactionRequest request, FraudDecision decision) {

        String ip = request.getIpAddress();
        String merchantId = request.getMerchantId();
        String cardNo = request.getCardNo();

        int score = decision.getRiskScore();

        if ("BLOCK".equalsIgnoreCase(decision.getDecision().name())) {

            IpRisk ipRisk = ipRiskRepository.findById(ip).orElse(new IpRisk());
            ipRisk.setIpAddress(ip);
            ipRisk.setRiskScore(100);
            ipRisk.setLastUpdated(LocalDateTime.now());
            ipRiskRepository.save(ipRisk);

            MerchantRisk merchantRisk = merchantRiskRepository.findById(merchantId).orElse(new MerchantRisk());
            merchantRisk.setMerchantId(merchantId);
            merchantRisk.setRiskScore(100);
            merchantRisk.setLastUpdated(LocalDateTime.now());
            merchantRiskRepository.save(merchantRisk);

            BlacklistedCard card = blacklistedCardRepository
                    .findByCardNo(cardNo)
                    .orElse(new BlacklistedCard());

            card.setCardNo(cardNo);
            card.setReason("Blocked transaction - fraud");
            card.setLastUpdated(LocalDateTime.now());
            blacklistedCardRepository.save(card);

            BlacklistedIp blacklistedIp = blacklistedIpDao
                    .findById(ip)
                    .orElse(new BlacklistedIp());

            blacklistedIp.setIp(ip);
            blacklistedIp.setLastUpdated(LocalDateTime.now());
            blacklistedIpDao.save(blacklistedIp);

            BlacklistedMerchant blacklistedMerchant = blacklistedMerchantDao
                    .findById(merchantId)
                    .orElse(new BlacklistedMerchant());

            blacklistedMerchant.setMerchantId(merchantId);
            blacklistedMerchant.setLastUpdated(LocalDateTime.now());
            blacklistedMerchantDao.save(blacklistedMerchant);
        }

        else if ("REVIEW".equalsIgnoreCase(decision.getDecision().name())) {

            updateRisk(ip, merchantId, 20);
        }

        else {
            updateRisk(ip, merchantId, -5);
        }
    }

    private void updateRisk(String ip, String merchantId, int delta) {

        IpRisk ipRisk = ipRiskRepository.findById(ip).orElse(new IpRisk());
        ipRisk.setIpAddress(ip);
        int newIpScore = Math.max(0, Math.min(100, ipRisk.getRiskScore() + delta));
        ipRisk.setRiskScore(newIpScore);
        ipRisk.setLastUpdated(LocalDateTime.now());
        ipRiskRepository.save(ipRisk);
        riskService.updateIpRisk(ip, newIpScore);


        MerchantRisk merchantRisk = merchantRiskRepository.findById(merchantId).orElse(new MerchantRisk());
        merchantRisk.setMerchantId(merchantId);
        int newMerchantScore = Math.max(0, Math.min(100, merchantRisk.getRiskScore() + delta));
        merchantRisk.setRiskScore(newMerchantScore);
        merchantRisk.setLastUpdated(LocalDateTime.now());
        merchantRiskRepository.save(merchantRisk);
        riskService.updateMerchantRisk(merchantId, newMerchantScore);
    }

}