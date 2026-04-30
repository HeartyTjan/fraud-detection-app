package com.interswitch.fraudtransactionapp.service;

import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.model.FraudDecision;

public interface FraudService {
    FraudDecision process(TransactionRequest request);
}
