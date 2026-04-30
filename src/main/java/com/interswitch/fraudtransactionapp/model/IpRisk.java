package com.interswitch.fraudtransactionapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class IpRisk {

    @Id
    private String ipAddress;
    private int riskScore;
    private LocalDateTime lastUpdated;
}