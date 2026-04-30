package com.interswitch.fraudtransactionapp.fraudEngine.model;

import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class RuleContext {

    private final TransactionRequest request;

    private Map<String, Integer> ipRiskMap;
    private Map<String, Integer> merchantRiskMap;
//    private Map<String, Boolean> blacklistedCards;

    private int cumulativeScore;
    private Map<String, String> triggeredRules;

    public void addTriggeredRule(String ruleName, String reason) {
        triggeredRules.put(ruleName, reason);
    }
}