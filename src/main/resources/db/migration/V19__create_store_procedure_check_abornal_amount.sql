CREATE OR ALTER PROCEDURE check_abnormal_amount_proc
    @p_card_no  VARCHAR(255),        -- IN
    @p_amount   DECIMAL(18, 2),      -- IN  (adjust precision/scale as needed)
    @p_decision VARCHAR(10) OUTPUT   -- OUT
    AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @avg_amount DECIMAL(18, 2);
    DECLARE @tx_count   INT;

SELECT
    @avg_amount = AVG(amount),
    @tx_count   = COUNT(*)
FROM transactions
WHERE card_no = @p_card_no;

-- Not enough history or no data
IF (@tx_count < 5 OR @avg_amount IS NULL)
BEGIN
        SET @p_decision = 'ALLOW';
        RETURN;
END;

    -- Abnormality checks
    IF @p_amount >= @avg_amount * 5
        SET @p_decision = 'BLOCK';
ELSE IF @p_amount >= @avg_amount * 3
        SET @p_decision = 'REVIEW';
ELSE
        SET @p_decision = 'ALLOW';
END;
GO