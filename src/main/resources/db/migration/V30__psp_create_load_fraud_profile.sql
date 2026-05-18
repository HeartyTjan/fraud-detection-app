CREATE OR ALTER PROCEDURE dbo.usp_LoadFraudProfile
    @CardNo            VARCHAR(30),
    @IpAddress         VARCHAR(50),
    @MerchantId        VARCHAR(50),
    @TransactionTime   DATETIME2
    AS
BEGIN
    SET NOCOUNT ON;

SELECT

    -- CARD VELOCITY
    SUM(CASE WHEN transaction_time >= DATEADD(MINUTE, -1, @TransactionTime) THEN 1 ELSE 0 END) AS card_last_1_min,
    SUM(CASE WHEN transaction_time >= DATEADD(HOUR, -1, @TransactionTime) THEN 1 ELSE 0 END) AS card_last_1_hour,
    SUM(CASE WHEN transaction_time >= DATEADD(HOUR, -24, @TransactionTime) THEN 1 ELSE 0 END) AS card_last_24_hour,

    -- IP VELOCITY
    SUM(CASE
            WHEN ip_address = @IpAddress
                AND transaction_time >= DATEADD(MINUTE, -1, @TransactionTime)
                THEN 1 ELSE 0
        END) AS ip_last_1_min,

    SUM(CASE
            WHEN ip_address = @IpAddress
                AND transaction_time >= DATEADD(HOUR, -1, @TransactionTime)
                THEN 1 ELSE 0
        END) AS ip_last_1_hour,

    -- AGGREGATES (EXCLUDE CURRENT TRANSACTION)
    AVG(amount) AS average_amount,
    COUNT(*) AS total_tx_count,

    -- LAST TRANSACTION (STRICTLY BEFORE CURRENT TX)
    MAX(CASE WHEN rn = 1 THEN latitude END) AS last_latitude,
    MAX(CASE WHEN rn = 1 THEN longitude END) AS last_longitude,
    MAX(CASE WHEN rn = 1 THEN transaction_time END) AS last_tx_time,

    -- RISK SCORES
    ISNULL((
               SELECT risk_score
               FROM ip_risk
               WHERE ip_address = @IpAddress
           ), 0) AS ip_risk_score,

    ISNULL((
               SELECT risk_score
               FROM merchant_risk
               WHERE merchant_id = @MerchantId
           ), 0) AS merchant_risk_score

FROM (
         SELECT *,
                ROW_NUMBER() OVER (
                   PARTITION BY card_no
                   ORDER BY transaction_time DESC
               ) AS rn
         FROM transactions
         WHERE card_no = @CardNo
           AND transaction_time < @TransactionTime
     ) t;

END;
GO