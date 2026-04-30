CREATE TABLE ip_risk (
     ip_address VARCHAR(255) NOT NULL,
     risk_score INT          NOT NULL,
     CONSTRAINT pk_iprisk PRIMARY KEY (ip_address)
);