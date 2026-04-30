CREATE OR ALTER FUNCTION check_abnormal_amount
    (
    @p_card_no VARCHAR(255),
    @p_amount  DECIMAL(18, 2)
    )
    RETURNS VARCHAR(10)
    AS
BEGIN
    DECLARE @avg_amount DECIMAL(18, 2);
    DECLARE @tx_count   INT;
    DECLARE @result     VARCHAR(10);

    SELECT
        @avg_amount = AVG(amount),
        @tx_count   = COUNT(*)
    FROM transactions
    WHERE card_no = @p_card_no;

    -- Insufficient history or no data
    IF (@tx_count < 5 OR @avg_amount IS NULL)
        SET @result = 'ALLOW';
    -- Abnormality checks
    ELSE IF @p_amount >= @avg_amount * 5
        SET @result = 'BLOCK';
    ELSE IF @p_amount >= @avg_amount * 3
        SET @result = 'REVIEW';
    ELSE
        SET @result = 'ALLOW';

    RETURN @result;
END;
GO
