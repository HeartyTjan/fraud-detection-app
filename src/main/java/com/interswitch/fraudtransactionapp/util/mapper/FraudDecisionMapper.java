package com.interswitch.fraudtransactionapp.util.mapper;



import com.interswitch.fraudtransactionapp.model.Decision;
import com.interswitch.fraudtransactionapp.model.FraudDecision;

import java.util.Map;

public final class  FraudDecisionMapper {

    public static FraudDecision mapToDecision(String decision, int score, Map<String, String> triggeredRules) {
        FraudDecision fraudDecision = new FraudDecision();
        fraudDecision.setDecision(Decision.valueOf(decision));
        fraudDecision.setRiskScore(score);
        fraudDecision.setTriggeredRules(triggeredRules);
        return fraudDecision;
    }

    public static FraudDecision allow(int score, Map<String, String> triggeredRules) {
        return mapToDecision("ALLOW", score, triggeredRules);
    }

    public static FraudDecision review(int score, Map<String, String> triggeredRules) {
        return mapToDecision("REVIEW", score, triggeredRules);
    }

    public static FraudDecision block(int score, Map<String, String> triggeredRules) {
        return mapToDecision("BLOCK", score, triggeredRules);
    }
}