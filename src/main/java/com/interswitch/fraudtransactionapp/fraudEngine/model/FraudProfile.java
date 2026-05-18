package com.interswitch.fraudtransactionapp.fraudEngine.model;


import com.interswitch.fraudtransactionapp.model.LastTransactionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FraudProfile {

    private final VelocityResult velocityResult;

    private final LastTransactionInfo lastTransaction;

    private final boolean firstTransaction;

    private final BigDecimal averageTransactionAmount;

    private final int transactionsLast24Hours;

    private final int ipRiskScore;

    private final int merchantRiskScore;

}
