EXEC sp_rename 'dbo.[transaction]', 'transactions';
GO

CREATE OR ALTER FUNCTION check_card_velocity
    (
    @p_card_no VARCHAR(255),
    @p_since   DATETIME2
    )
    RETURNS INT
    AS
BEGIN
    DECLARE @tx_count INT;

SELECT @tx_count = COUNT(*)
FROM transactions
WHERE card_no = @p_card_no
  AND created_at >= @p_since;

RETURN @tx_count;
END;
GO