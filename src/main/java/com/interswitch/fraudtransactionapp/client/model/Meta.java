package com.interswitch.fraudtransactionapp.client.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    private long generatedAt;
    private int scoreMinimum;
    private int limit;
    private int count;
}