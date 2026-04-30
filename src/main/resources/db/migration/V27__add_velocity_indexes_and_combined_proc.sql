-- Add indexes for velocity checks
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_card_created')
CREATE INDEX idx_card_created ON transactions(card_no, created_at);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_ip_txtime')
CREATE INDEX idx_ip_txtime ON transactions(ip_address, transaction_time);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_card_lat_txtime')
CREATE INDEX idx_card_lat_txtime ON transactions(card_no, latitude, transaction_time DESC);
GO

CREATE OR ALTER PROCEDURE check_all_velocity
    @p_card_no      VARCHAR(255),
    @p_ip_address   VARCHAR(45),
    @p_now          DATETIME2,
    @card_1min      INT OUTPUT,
    @card_1hour     INT OUTPUT,
    @card_24hour    INT OUTPUT,
    @ip_1min        INT OUTPUT,
    @ip_1hour       INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @since_1min DATETIME2 = DATEADD(SECOND, -60, @p_now);
    DECLARE @since_1hour DATETIME2 = DATEADD(SECOND, -3600, @p_now);
    DECLARE @since_24hour DATETIME2 = DATEADD(SECOND, -86400, @p_now);

    -- Card velocity counts
    SELECT @card_1min = COUNT(*) FROM transactions
    WHERE card_no = @p_card_no AND created_at >= @since_1min;

    SELECT @card_1hour = COUNT(*) FROM transactions
    WHERE card_no = @p_card_no AND created_at >= @since_1hour;

    SELECT @card_24hour = COUNT(*) FROM transactions
    WHERE card_no = @p_card_no AND created_at >= @since_24hour;

    -- IP velocity counts
    SELECT @ip_1min = COUNT(*) FROM transactions
    WHERE ip_address = @p_ip_address AND transaction_time >= @since_1min;

    SELECT @ip_1hour = COUNT(*) FROM transactions
    WHERE ip_address = @p_ip_address AND transaction_time >= @since_1hour;
END;
GO
