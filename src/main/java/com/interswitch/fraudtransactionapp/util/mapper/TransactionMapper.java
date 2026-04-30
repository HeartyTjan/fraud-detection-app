package com.interswitch.fraudtransactionapp.util.mapper;


import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.model.Transactions;
import com.interswitch.fraudtransactionapp.model.FraudDecision;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public final class TransactionMapper {

    private TransactionMapper() {}

    public static Transactions toEntity(TransactionRequest request, FraudDecision decision) {
        Transactions transaction = new Transactions();

        transaction.setCardNo(request.getCardNo());
        transaction.setAmount(request.getAmount());
        transaction.setMerchantId(request.getMerchantId());
        transaction.setIpAddress(request.getIpAddress());
        transaction.setType(request.getType());
        transaction.setTransactionTime(request.getTransactionTime());

        transaction.setRiskScore(decision.getRiskScore());
        transaction.setDecision(decision.getDecision().name());
        transaction.setTriggeredRules(formatTriggeredRules(decision.getTriggeredRules()));


        transaction.setDeviceFingerprint(request.getDeviceFingerprint());
        transaction.setUserAgent(request.getUserAgent());
        transaction.setSessionId(request.getSessionId());
        transaction.setLatitude(request.getLatitude());
        transaction.setLongitude(request.getLongitude());
        transaction.setCountryCode(request.getCountryCode());
        transaction.setCurrency(request.getCurrency());
        transaction.setCardBin(request.getCardBin());
        transaction.setMerchantCategory(request.getMerchantCategory());
        transaction.setChannelType(request.getChannelType());

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        return transaction;
    }

    private static String formatTriggeredRules(Map<String, String> triggeredRules) {
        if (triggeredRules == null || triggeredRules.isEmpty()) {
            return null;
        }
        return triggeredRules.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining("|"));
    }
}