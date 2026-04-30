package com.interswitch.fraudtransactionapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_card_no", columnList = "cardNo"),
        @Index(name = "idx_ip_address", columnList = "ipAddress"),
        @Index(name = "idx_merchant_id", columnList = "merchantId"),
        @Index(name = "idx_device_fingerprint", columnList = "deviceFingerprint"),
        @Index(name = "idx_transaction_time", columnList = "transactionTime")
})
@Getter
@Setter
public class Transactions extends AuditBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cardNo;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private String merchantId;

    @Column(length = 45)
    private String ipAddress;

    private int riskScore;

    @Column(nullable = false, length = 20)
    private String decision;

    @Column(nullable = false, columnDefinition = "datetime2")
    private Instant transactionTime;

    @Column(length = 50)
    private String type;

    @Column(length = 256)
    private String deviceFingerprint;

    @Column(length = 512)
    private String userAgent;

    @Column(length = 100)
    private String sessionId;

    private Double latitude;

    private Double longitude;

    @Column(length = 3)
    private String countryCode;

    @Column(length = 3)
    private String currency;

    @Column(length = 8)
    private String cardBin;

    @Column(length = 10)
    private String merchantCategory;

    @Column(length = 20)
    private String channelType;

    @Column(columnDefinition = "TEXT")
    private String triggeredRules;
}