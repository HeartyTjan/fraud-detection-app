package com.interswitch.fraudtransactionapp.model;

public record RiskUpdateResult(
        String ip,
        String merchantId,
        int ipScore,
        int merchantScore
) {}