package com.interswitch.fraudtransactionapp.service.impl;

import com.interswitch.fraudtransactionapp.config.FraudKeyBuilder;
import com.interswitch.fraudtransactionapp.config.TrackExecution;
import com.interswitch.fraudtransactionapp.dao.BlackListedIpDao;
import com.interswitch.fraudtransactionapp.dao.BlackListedMerchantDao;
import com.interswitch.fraudtransactionapp.dao.FraudDao;
import com.interswitch.fraudtransactionapp.dao.TransactionJdbcDao;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.FraudEngine;
import com.interswitch.fraudtransactionapp.model.*;
import com.interswitch.fraudtransactionapp.repository.*;
import com.interswitch.fraudtransactionapp.service.FraudService;
import com.interswitch.fraudtransactionapp.util.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@TrackExecution
public class FraudServiceImpl implements FraudService {

    private final FraudEngine fraudEngine;
    private final RiskService riskService;
    private final TransactionJdbcDao transactionJdbcDao;
    private final FraudDao fraudDao;
    private final FraudProfileCacheService fraudProfileCacheService;
    private final FraudKeyBuilder keyBuilder;
    private final BlacklistCache blacklistCache;

    @Override
    public FraudDecision process(TransactionRequest request) {
        FraudDecision decision = fraudEngine.evaluate(request);

        persistDecision(request, decision);

        return decision;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistDecision(TransactionRequest request, FraudDecision decision) {
        handlePostDecision(request, decision);
        Transactions transaction = TransactionMapper.toEntity(request, decision);
        transactionJdbcDao.save(transaction);
    }

    private void handlePostDecision(
            TransactionRequest request,
            FraudDecision decision
    ) {

        String ip = request.getIpAddress();
        String merchantId = request.getMerchantId();
        String cardNo = request.getCardNo();

        FraudDecisionType type = decision.getDecision();

        switch (type) {

            case BLOCK -> {
                fraudDao.applyBlockActions(cardNo,ip,merchantId);

                String key = keyBuilder.build(request);
                fraudProfileCacheService.invalidate(key);
                blacklistCache.addCard(cardNo);
                blacklistCache.addIp(ip);
                blacklistCache.addMerchant(merchantId);
            }

            case REVIEW -> fraudDao.updateRiskScores(ip, merchantId, 20);

            case ALLOW -> fraudDao.updateRiskScores(ip, merchantId, -5);
        }
    }


}