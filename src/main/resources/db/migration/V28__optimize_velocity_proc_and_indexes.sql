-- Add composite index for card velocity (covers both created_at and transaction_time)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_card_txtime')
CREATE INDEX idx_card_txtime ON transactions(card_no, transaction_time);
GO

-- Optimized procedure: single query with conditional aggregation instead of 5 separate queries
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

    -- Card velocity: single query with conditional counts
    SELECT
        @card_1min = SUM(CASE WHEN created_at >= @since_1min THEN 1 ELSE 0 END),
        @card_1hour = SUM(CASE WHEN created_at >= @since_1hour THEN 1 ELSE 0 END),
        @card_24hour = COUNT(*)
    FROM transactions WITH (NOLOCK)
    WHERE card_no = @p_card_no AND created_at >= @since_24hour;

    -- IP velocity: single query with conditional counts
    SELECT
        @ip_1min = SUM(CASE WHEN transaction_time >= @since_1min THEN 1 ELSE 0 END),
        @ip_1hour = COUNT(*)
    FROM transactions WITH (NOLOCK)
    WHERE ip_address = @p_ip_address AND transaction_time >= @since_1hour;

    -- Handle NULLs
    SET @card_1min = ISNULL(@card_1min, 0);
    SET @card_1hour = ISNULL(@card_1hour, 0);
    SET @card_24hour = ISNULL(@card_24hour, 0);
    SET @ip_1min = ISNULL(@ip_1min, 0);
    SET @ip_1hour = ISNULL(@ip_1hour, 0);
END;
GO

-- Optimize abnormal amount check with index
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_card_amount')
CREATE INDEX idx_card_amount ON transactions(card_no, amount);
GO
