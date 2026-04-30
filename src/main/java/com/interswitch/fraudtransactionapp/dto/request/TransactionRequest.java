package com.interswitch.fraudtransactionapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class TransactionRequest {

    @NotBlank(message = "Card number is required")
    private String cardNo;

    @NotNull(message = "Transaction amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Merchant ID is required")
    private String merchantId;

    @NotNull(message = "Transaction time is required")
    private Instant transactionTime;

    @NotBlank(message = "Channel type is required")
    private String channelType;

    @NotBlank(message = "IP address is required for fraud analysis")
    private String ipAddress;

    @NotBlank(message = "Device fingerprint is required for fraud detection")
    private String deviceFingerprint;

    @NotBlank(message = "User agent is required for risk analysis")
    private String userAgent;


    private String sessionId;

    private Double latitude;

    private Double longitude;

    private String countryCode;

    private String currency;

    private String cardBin;

    private String merchantCategory;

    private String type;
}