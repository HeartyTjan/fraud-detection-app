package com.interswitch.fraudtransactionapp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FraudDecision {
    private int riskScore;
    private Decision decision;
    private Map<String, String> triggeredRules;


    public void setRiskScore(int riskScore) {
        this.riskScore = Math.max(0, Math.min(100, riskScore));
    }

    public void addRiskScore(int delta) {
        this.riskScore = Math.max(0, Math.min(100, this.riskScore + delta));
    }
}