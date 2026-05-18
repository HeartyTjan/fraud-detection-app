CREATE PROCEDURE usp_apply_block_actions
    @cardNo NVARCHAR(50),
    @ip NVARCHAR(50),
    @merchantId NVARCHAR(50)
AS
BEGIN

    -- update IP risk
UPDATE ip_risk
SET risk_score = 100,
    last_updated = GETDATE()
WHERE ip_address = @ip;

-- update merchant risk
UPDATE merchant_risk
SET risk_score = 100,
    last_updated = GETDATE()
WHERE merchant_id = @merchantId;

-- blacklist card
MERGE blacklisted_card AS target
    USING (SELECT @cardNo AS card_no) AS src
    ON target.card_no = src.card_no
    WHEN MATCHED THEN
UPDATE SET reason = 'BLOCKED_FRAUD', last_updated = GETDATE()
    WHEN NOT MATCHED THEN
INSERT (card_no, reason, last_updated)
VALUES (@cardNo, 'BLOCKED_FRAUD', GETDATE());

-- blacklist IP
MERGE blacklisted_ip AS target
    USING (SELECT @ip AS ip) AS src
    ON target.ip = src.ip
    WHEN NOT MATCHED THEN
    INSERT (ip, last_updated)
    VALUES (@ip, GETDATE());

-- blacklist merchant
MERGE blacklisted_merchant AS target
    USING (SELECT @merchantId AS merchant_id) AS src
    ON target.merchant_id = src.merchant_id
    WHEN NOT MATCHED THEN
    INSERT (merchant_id, last_updated)
    VALUES (@merchantId, GETDATE());

END