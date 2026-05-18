package com.interswitch.fraudtransactionapp.service.impl;

import com.interswitch.fraudtransactionapp.config.FraudKeyBuilder;
import com.interswitch.fraudtransactionapp.dao.BlackListedIpDao;
import com.interswitch.fraudtransactionapp.dao.BlackListedMerchantDao;
import com.interswitch.fraudtransactionapp.dao.FraudDao;
import com.interswitch.fraudtransactionapp.dao.TransactionJdbcDao;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.fraudEngine.FraudEngine;
import com.interswitch.fraudtransactionapp.model.*;
import com.interswitch.fraudtransactionapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudServiceImplTest {

    @InjectMocks
    private FraudServiceImpl fraudService;

    @Mock
    private FraudEngine fraudEngine;

    @Mock
    private RiskService riskService;

    @Mock
    private TransactionJdbcDao transactionJdbcDao;

    @Mock
    private FraudDao fraudDao;

    @Mock
    private FraudProfileCacheService fraudProfileCacheService;

    @Mock
    private FraudKeyBuilder keyBuilder;

    @Mock
    private BlacklistCache blacklistCache;

    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        request = new TransactionRequest(
                "1234567890123456",
                BigDecimal.valueOf(1000.0),
                "M123",
                Instant.now(),
                "PURCHASE",
                "192.168.1.1",
                null, null, null, null, null, null, null, null, null, null
        );
    }

    @Test
    void shouldBlockTransactionAndBlacklistEntities() {

        FraudDecision decision = new FraudDecision();
        decision.setDecision(FraudDecisionType.BLOCK);
        decision.setRiskScore(95);

        when(fraudEngine.evaluate(any(TransactionRequest.class))).thenReturn(decision);
        when(keyBuilder.build(any())).thenReturn("cache-key");

        FraudDecision result = fraudService.process(request);

        assertThat(result.getDecision()).isEqualTo(FraudDecisionType.BLOCK);

        verify(fraudDao).applyBlockActions(
                eq("1234567890123456"),
                eq("192.168.1.1"),
                eq("M123")
        );

        verify(transactionJdbcDao).save(any(Transactions.class));

        verify(fraudProfileCacheService).invalidate("cache-key");

        verify(blacklistCache).addCard("1234567890123456");
        verify(blacklistCache).addIp("192.168.1.1");
        verify(blacklistCache).addMerchant("M123");
    }

    @Test
    void shouldReviewTransactionAndIncreaseRisk() {

        FraudDecision decision = new FraudDecision();
        decision.setDecision(FraudDecisionType.REVIEW);

        when(fraudEngine.evaluate(any(TransactionRequest.class))).thenReturn(decision);

        FraudDecision result = fraudService.process(request);

        assertThat(result.getDecision()).isEqualTo(FraudDecisionType.REVIEW);

        verify(fraudDao).updateRiskScores("192.168.1.1", "M123", 20);
        verify(transactionJdbcDao).save(any(Transactions.class));
    }

    @Test
    void shouldApproveTransactionAndReduceRisk() {

        FraudDecision decision = new FraudDecision();
        decision.setDecision(FraudDecisionType.ALLOW);

        when(fraudEngine.evaluate(any(TransactionRequest.class))).thenReturn(decision);

        FraudDecision result = fraudService.process(request);

        assertThat(result.getDecision()).isEqualTo(FraudDecisionType.ALLOW);

        verify(fraudDao).updateRiskScores("192.168.1.1", "M123", -5);
        verify(transactionJdbcDao).save(any(Transactions.class));
    }
}