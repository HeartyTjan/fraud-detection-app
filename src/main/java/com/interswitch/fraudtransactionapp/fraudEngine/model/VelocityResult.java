package com.interswitch.fraudtransactionapp.fraudEngine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VelocityResult {
    public int cardLast1Min;
    public int cardLast1Hour;
    public int cardLast24Hour;
    public int ipLast1Min;
    public int ipLast1Hour;
}
