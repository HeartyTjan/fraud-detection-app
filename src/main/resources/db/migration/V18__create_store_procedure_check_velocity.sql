CREATE OR ALTER PROCEDURE check_card_velocity_proc
    @p_card_no  VARCHAR(255),      -- IN
    @p_since    DATETIME2,         -- IN
    @p_tx_count INT OUTPUT         -- OUT
    AS
BEGIN
    SET NOCOUNT ON;

SELECT @p_tx_count = COUNT(*)
FROM transactions
WHERE card_no   = @p_card_no
  AND created_at >= @p_since;
END;
GO