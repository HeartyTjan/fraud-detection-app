package com.interswitch.fraudtransactionapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "blacklisted_merchant")
public class BlacklistedMerchant{
    @Id
    private String merchantId;
    private LocalDateTime lastUpdated;
}


