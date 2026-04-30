CREATE OR ALTER FUNCTION check_card_velocity
    (
    @p_card_no VARCHAR(255),
    @p_since   DATETIME2   -- or DATETIME, to match your table definition
    )
    RETURNS INT
    AS
BEGIN
    DECLARE @tx_count INT;

SELECT @tx_count = COUNT(*)
FROM transactions
WHERE card_no   = @p_card_no
  AND created_at >= @p_since;

RETURN @tx_count;
END;
GO