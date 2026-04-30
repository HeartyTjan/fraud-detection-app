CREATE TABLE fraud_event (
     id          BIGINT IDENTITY(1,1)       NOT NULL,
     created_at  DATETIME2                  NOT NULL,
     updated_at  DATETIME2                  NOT NULL,
     created_by  VARCHAR(255)              NULL,
     updated_by  VARCHAR(255)              NULL,
     deleted     BIT                        NOT NULL,
     card_no     VARCHAR(255)              NULL,
     amount      DECIMAL(18, 2)            NULL,
     merchant_id VARCHAR(255)              NULL,
     ip_address  VARCHAR(255)              NULL,
     risk_score  INT                       NOT NULL,
     decision    VARCHAR(255)              NULL,
     CONSTRAINT pk_fraud_event PRIMARY KEY (id)
);