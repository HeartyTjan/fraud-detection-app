CREATE INDEX idx_tx_decision_time
    ON transactions (decision, transaction_time DESC);