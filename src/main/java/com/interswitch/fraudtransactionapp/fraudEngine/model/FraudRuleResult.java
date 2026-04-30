package com.interswitch.fraudtransactionapp.fraudEngine.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FraudRuleResult {

    private int score;
    private String reason;
    private boolean stopProcessing;

}
