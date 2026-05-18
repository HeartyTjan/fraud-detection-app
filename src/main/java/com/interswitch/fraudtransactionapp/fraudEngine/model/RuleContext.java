package com.interswitch.fraudtransactionapp.fraudEngine.model;

import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;

public record RuleContext(
        TransactionRequest request,
        FraudProfile fraudProfile
) {}