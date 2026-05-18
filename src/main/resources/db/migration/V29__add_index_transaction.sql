-- CARD VELOCITY INDEX
IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_tx_card_time')
DROP INDEX idx_tx_card_time ON transactions;
GO

CREATE INDEX idx_tx_card_time
    ON transactions (card_no, transaction_time DESC);
GO


-- IP VELOCITY INDEX
IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_tx_ip_time')
DROP INDEX idx_tx_ip_time ON transactions;
GO

CREATE INDEX idx_tx_ip_time
    ON transactions (ip_address, transaction_time DESC);
GO


-- CARD AMOUNT INDEX
IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_tx_card_amount')
DROP INDEX idx_tx_card_amount ON transactions;
GO

CREATE INDEX idx_tx_card_amount
    ON transactions (card_no)
    INCLUDE (amount);
GO


-- GEO VELOCITY INDEX
IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_tx_card_geo_time')
DROP INDEX idx_tx_card_geo_time ON transactions;
GO

CREATE INDEX idx_tx_card_geo_time
    ON transactions (card_no, transaction_time DESC)
    INCLUDE (latitude, longitude);
GO