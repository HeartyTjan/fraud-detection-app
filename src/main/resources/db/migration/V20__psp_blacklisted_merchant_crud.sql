
IF OBJECT_ID('dbo.blacklisted_merchant', 'U') IS NULL
BEGIN
CREATE TABLE dbo.blacklisted_merchant (
                                          merchant_id  VARCHAR(255) NOT NULL PRIMARY KEY,
                                          last_updated DATETIME2(0) NOT NULL
);
END;
GO


CREATE OR ALTER PROCEDURE dbo.create_blacklisted_merchant
    @p_merchant_id  VARCHAR(255),
    @p_last_updated DATETIME2(0) = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    IF @p_last_updated IS NULL
        SET @p_last_updated = SYSDATETIME();

INSERT INTO dbo.blacklisted_merchant (merchant_id, last_updated)
VALUES (@p_merchant_id, @p_last_updated);

SELECT merchant_id, last_updated
FROM dbo.blacklisted_merchant
WHERE merchant_id = @p_merchant_id;
END;
GO


CREATE OR ALTER PROCEDURE dbo.update_blacklisted_merchant
    @p_merchant_id  VARCHAR(255),
    @p_last_updated DATETIME2(0) = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    IF @p_last_updated IS NULL
        SET @p_last_updated = SYSDATETIME();

UPDATE dbo.blacklisted_merchant
SET last_updated = @p_last_updated
WHERE merchant_id = @p_merchant_id;

SELECT merchant_id, last_updated
FROM dbo.blacklisted_merchant
WHERE merchant_id = @p_merchant_id;
END;
GO

CREATE OR ALTER PROCEDURE dbo.delete_blacklisted_merchant
    @p_merchant_id VARCHAR(255),
    @rows_deleted  INT OUTPUT
    AS
BEGIN
    SET NOCOUNT ON;

DELETE FROM dbo.blacklisted_merchant
WHERE merchant_id = @p_merchant_id;

SET @rows_deleted = @@ROWCOUNT;
END;
GO


CREATE OR ALTER PROCEDURE dbo.upsert_blacklisted_merchant
    @p_merchant_id  VARCHAR(255),
    @p_last_updated DATETIME2(0) = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    IF @p_last_updated IS NULL
        SET @p_last_updated = SYSDATETIME();

MERGE dbo.blacklisted_merchant AS target
    USING (SELECT @p_merchant_id AS merchant_id,
    @p_last_updated AS last_updated) AS src
    ON target.merchant_id = src.merchant_id
    WHEN MATCHED THEN
UPDATE SET last_updated = src.last_updated
    WHEN NOT MATCHED THEN
INSERT (merchant_id, last_updated)
VALUES (src.merchant_id, src.last_updated);

SELECT merchant_id, last_updated
FROM dbo.blacklisted_merchant
WHERE merchant_id = @p_merchant_id;
END;
GO


CREATE OR ALTER PROCEDURE dbo.find_blacklisted_merchant_by_id
    @p_merchant_id VARCHAR(255)
    AS
BEGIN
    SET NOCOUNT ON;

SELECT merchant_id, last_updated
FROM dbo.blacklisted_merchant
WHERE merchant_id = @p_merchant_id;
END;
GO

CREATE OR ALTER PROCEDURE dbo.find_all_blacklisted_merchant
    AS
BEGIN
    SET NOCOUNT ON;

SELECT merchant_id, last_updated
FROM dbo.blacklisted_merchant;
END;
GO