package com.interswitch.fraudtransactionapp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class LastTransactionInfo {
    private Double latitude;
    private Double longitude;
    private Instant transactionTime;
}