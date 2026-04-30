package com.interswitch.fraudtransactionapp.client.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Entity
public class ProviderBlacklistedIP {
    @Id
    private String ip;
    private int confidenceLevel;
    private long lastSeen;
    private int sessions;
    private String protocols;
    private String countryCode;
}
