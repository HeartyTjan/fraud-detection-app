package com.interswitch.fraudtransactionapp.config;

import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import org.springframework.stereotype.Component;

@Component
public class FraudKeyBuilder {

    public String build(TransactionRequest req) {
        return req.getCardNo() + ":" +
                req.getIpAddress() + ":" +
                req.getMerchantId();
    }
}