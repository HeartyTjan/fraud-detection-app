package com.interswitch.fraudtransactionapp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProviderBlacklistedIPDTO {
    private String ip;
    private int confidenceLevel;
    private long lastSeen;
    private int sessions;
    private List<String> protocols;
    private String countryCode;
}