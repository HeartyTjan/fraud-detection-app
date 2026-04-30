ALTER TABLE transactions ADD transaction_time DATETIME2 NOT NULL DEFAULT GETDATE();
ALTER TABLE transactions ADD type VARCHAR(50);
ALTER TABLE transactions ADD device_fingerprint VARCHAR(256);
ALTER TABLE transactions ADD user_agent VARCHAR(512);
ALTER TABLE transactions ADD session_id VARCHAR(100);
ALTER TABLE transactions ADD latitude FLOAT;
ALTER TABLE transactions ADD longitude FLOAT;
ALTER TABLE transactions ADD country_code VARCHAR(3);
ALTER TABLE transactions ADD currency VARCHAR(3);
ALTER TABLE transactions ADD card_bin VARCHAR(8);
ALTER TABLE transactions ADD merchant_category VARCHAR(10);
ALTER TABLE transactions ADD channel_type VARCHAR(20);
ALTER TABLE transactions ADD triggered_rules TEXT;

CREATE INDEX idx_card_no ON transactions(card_no);
CREATE INDEX idx_ip_address ON transactions(ip_address);
CREATE INDEX idx_merchant_id ON transactions(merchant_id);
CREATE INDEX idx_device_fingerprint ON transactions(device_fingerprint);
CREATE INDEX idx_transaction_time ON transactions(transaction_time);

CREATE INDEX idx_card_time ON transactions(card_no, transaction_time);
CREATE INDEX idx_ip_time ON transactions(ip_address, transaction_time);
CREATE INDEX idx_device_time ON transactions(device_fingerprint, transaction_time);