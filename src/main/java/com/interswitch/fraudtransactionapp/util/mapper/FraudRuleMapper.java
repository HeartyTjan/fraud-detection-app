package com.interswitch.fraudtransactionapp.util.mapper;

import com.interswitch.fraudtransactionapp.fraudEngine.model.FraudRuleResult;

public final class FraudRuleMapper {

    public static FraudRuleResult mapToResult(int score, String reason, boolean stopProcessing) {
        return FraudRuleResult.builder()
                .score(score)
                .reason(reason)
                .stopProcessing(stopProcessing)
                .build();
    }
}
