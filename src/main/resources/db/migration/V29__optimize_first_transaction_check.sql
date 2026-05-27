-- Index for first transaction check (card_no only, covering index)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_card_no_only')
CREATE INDEX idx_card_no_only ON transactions(card_no);
GO
