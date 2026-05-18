CREATE OR ALTER PROCEDURE usp_update_risk_scores
    @ip NVARCHAR(50),
    @merchantId NVARCHAR(50),
    @delta INT
    AS
BEGIN
    SET NOCOUNT ON;

    -- IP risk
MERGE ip_risk AS t
    USING (SELECT @ip AS ip_address) s
    ON t.ip_address = s.ip_address
    WHEN MATCHED THEN
UPDATE SET
    risk_score =
    CASE
    WHEN risk_score + @delta < 0 THEN 0
    WHEN risk_score + @delta > 100 THEN 100
    ELSE risk_score + @delta
END,
            last_updated = GETDATE()
    WHEN NOT MATCHED THEN
        INSERT (ip_address, risk_score, last_updated)
        VALUES (
            @ip,
            CASE WHEN @delta < 0 THEN 0 ELSE @delta END,
            GETDATE()
        );

    -- Merchant risk
MERGE merchant_risk AS t
    USING (SELECT @merchantId AS merchant_id) s
    ON t.merchant_id = s.merchant_id
    WHEN MATCHED THEN
UPDATE SET
    risk_score =
    CASE
    WHEN risk_score + @delta < 0 THEN 0
    WHEN risk_score + @delta > 100 THEN 100
    ELSE risk_score + @delta
END,
            last_updated = GETDATE()
    WHEN NOT MATCHED THEN
        INSERT (merchant_id, risk_score, last_updated)
        VALUES (
            @merchantId,
            CASE WHEN @delta < 0 THEN 0 ELSE @delta END,
            GETDATE()
        );

    -- return scores
SELECT
    (SELECT risk_score FROM ip_risk WHERE ip_address = @ip) AS ip_score,
    (SELECT risk_score FROM merchant_risk WHERE merchant_id = @merchantId) AS merchant_score;

END;