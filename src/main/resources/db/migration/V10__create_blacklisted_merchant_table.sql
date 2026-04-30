CREATE TABLE blacklisted_merchant (
  merchant_id  VARCHAR(255) NOT NULL,
  last_updated DATETIME2    NULL,
  CONSTRAINT pk_blacklistedmerchant PRIMARY KEY (merchant_id)
);