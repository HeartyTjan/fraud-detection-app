package com.interswitch.fraudtransactionapp.fraudEngine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VelocityResult {
    private int cardLast1Min;
    private int cardLast1Hour;
    private int cardLast24Hour;
    private int ipLast1Min;
    private int ipLast1Hour;
}
