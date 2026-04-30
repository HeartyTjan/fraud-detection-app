CREATE INDEX idx_decision ON transactions(decision);
GO

CREATE INDEX idx_decision_time ON transactions(decision, transaction_time DESC);