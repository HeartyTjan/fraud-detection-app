ALTER TABLE ip_risk
    ADD last_updated DATETIME2 NOT NULL
    CONSTRAINT DF_ip_risk_last_updated DEFAULT SYSDATETIME();
GO

ALTER TABLE merchant_risk
    ADD last_updated DATETIME2 NOT NULL
    CONSTRAINT DF_merchant_risk_last_updated DEFAULT SYSDATETIME();
GO

ALTER TABLE blacklisted_card
    ADD last_updated DATETIME2 NOT NULL
    CONSTRAINT DF_blacklisted_card_last_updated DEFAULT SYSDATETIME();
GO