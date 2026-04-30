CREATE TABLE merchant_risk (
   merchant_id VARCHAR(255) NOT NULL,
   risk_score  INT          NOT NULL,
   CONSTRAINT pk_merchantrisk PRIMARY KEY (merchant_id)
);