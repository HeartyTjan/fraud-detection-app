package com.interswitch.fraudtransactionapp.service.impl;

import com.interswitch.fraudtransactionapp.dao.BlackListedIpDao;
import com.interswitch.fraudtransactionapp.dao.BlackListedMerchantDao;
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
    private IpRiskRepository ipRiskRepository;

    @Mock
    private MerchantRiskRepository merchantRiskRepository;

    @Mock
    private BlacklistedCardRepository blacklistedCardRepository;

    @Mock
    private BlackListedMerchantDao blacklistedMerchantDao;

    @Mock
    private BlackListedIpDao blacklistedIpDao;

    @Mock
    private RiskService riskService;

//    @Mock
//   private TransactionRepository transactionRepository;
    @Mock
    private TransactionJdbcDao transactionJdbcDao;

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
        decision.setDecision(Decision.BLOCK);
        decision.setRiskScore(95);

        when(fraudEngine.evaluate(any(TransactionRequest.class))).thenReturn(decision);
        when(ipRiskRepository.findById("192.168.1.1")).thenReturn(Optional.empty());
        when(merchantRiskRepository.findById("M123")).thenReturn(Optional.empty());
        when(blacklistedCardRepository.findByCardNo("1234567890123456")).thenReturn(Optional.empty());
        when(blacklistedIpDao.findById("192.168.1.1")).thenReturn(Optional.empty());
        when(blacklistedMerchantDao.findById("M123")).thenReturn(Optional.empty());

        FraudDecision result = fraudService.process(request);

        assertThat(result.getDecision()).isEqualTo(Decision.BLOCK);

        verify(ipRiskRepository).save(any(IpRisk.class));
        verify(merchantRiskRepository).save(any(MerchantRisk.class));
        verify(blacklistedCardRepository).save(any(BlacklistedCard.class));
        verify(blacklistedIpDao).save(any(BlacklistedIp.class));
        verify(blacklistedMerchantDao).save(any(BlacklistedMerchant.class));
        verify(transactionJdbcDao).save(any(Transactions.class));
    }

    @Test
    void shouldReviewTransactionAndIncreaseRisk() {
        FraudDecision decision = new FraudDecision();
        decision.setDecision(Decision.REVIEW);
        decision.setRiskScore(50);

        when(fraudEngine.evaluate(any(TransactionRequest.class))).thenReturn(decision);
        when(ipRiskRepository.findById("192.168.1.1")).thenReturn(Optional.empty());
        when(merchantRiskRepository.findById("M123")).thenReturn(Optional.empty());

        FraudDecision result = fraudService.process(request);

        assertThat(result.getDecision()).isEqualTo(Decision.REVIEW);
        verify(ipRiskRepository).save(argThat(ip -> ip.getRiskScore() == 20));
        verify(merchantRiskRepository).save(argThat(m -> m.getRiskScore() == 20));
    }

    @Test
    void shouldApproveTransactionAndReduceRisk() {
        FraudDecision decision = new FraudDecision();
        decision.setDecision(Decision.ALLOW);
        decision.setRiskScore(10);

        when(fraudEngine.evaluate(any(TransactionRequest.class))).thenReturn(decision);
        when(ipRiskRepository.findById("192.168.1.1")).thenReturn(Optional.empty());
        when(merchantRiskRepository.findById("M123")).thenReturn(Optional.empty());

        FraudDecision result = fraudService.process(request);

        assertThat(result.getDecision()).isEqualTo(Decision.ALLOW);
        verify(ipRiskRepository).save(argThat(ip -> ip.getRiskScore() == 0));
        verify(merchantRiskRepository).save(argThat(m -> m.getRiskScore() == 0));
    }
}