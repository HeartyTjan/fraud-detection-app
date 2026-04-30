
IF OBJECT_ID('dbo.blacklisted_ip', 'U') IS NULL
BEGIN
CREATE TABLE dbo.blacklisted_ip (
                                    ip           VARCHAR(255) NOT NULL PRIMARY KEY,
                                    last_updated DATETIME2(0) NOT NULL
);
END;
GO


CREATE OR ALTER PROCEDURE dbo.create_blacklisted_ip
    @p_ip           VARCHAR(255),
    @p_last_updated DATETIME2(0) = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    IF @p_last_updated IS NULL
        SET @p_last_updated = SYSDATETIME();

INSERT INTO dbo.blacklisted_ip (ip, last_updated)
VALUES (@p_ip, @p_last_updated);

SELECT ip, last_updated
FROM dbo.blacklisted_ip
WHERE ip = @p_ip;
END;
GO


CREATE OR ALTER PROCEDURE dbo.update_blacklisted_ip
    @p_ip           VARCHAR(255),
    @p_last_updated DATETIME2(0) = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    IF @p_last_updated IS NULL
        SET @p_last_updated = SYSDATETIME();

UPDATE dbo.blacklisted_ip
SET last_updated = @p_last_updated
WHERE ip = @p_ip;

SELECT ip, last_updated
FROM dbo.blacklisted_ip
WHERE ip = @p_ip;
END;
GO

CREATE OR ALTER PROCEDURE dbo.delete_blacklisted_ip
    @p_ip         VARCHAR(255),
    @rows_deleted INT OUTPUT
    AS
BEGIN
    SET NOCOUNT ON;

DELETE FROM dbo.blacklisted_ip
WHERE ip = @p_ip;

SET @rows_deleted = @@ROWCOUNT;
END;
GO


CREATE OR ALTER PROCEDURE dbo.upsert_blacklisted_ip
    @p_ip           VARCHAR(255),
    @p_last_updated DATETIME2(0) = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    IF @p_last_updated IS NULL
        SET @p_last_updated = SYSDATETIME();

MERGE dbo.blacklisted_ip AS target
    USING (SELECT @p_ip AS ip,
    @p_last_updated AS last_updated) AS src
    ON target.ip = src.ip
    WHEN MATCHED THEN
UPDATE SET last_updated = src.last_updated
    WHEN NOT MATCHED THEN
INSERT (ip, last_updated)
VALUES (src.ip, src.last_updated);

-- Return the row after upsert
SELECT ip, last_updated
FROM dbo.blacklisted_ip
WHERE ip = @p_ip;
END;
GO


CREATE OR ALTER PROCEDURE dbo.find_blacklisted_ip_by_id
    @p_ip VARCHAR(255)
    AS
BEGIN
    SET NOCOUNT ON;

SELECT ip, last_updated
FROM dbo.blacklisted_ip
WHERE ip = @p_ip;
END;
GO


CREATE OR ALTER PROCEDURE dbo.find_all_blacklisted_ip
    AS
BEGIN
    SET NOCOUNT ON;

SELECT ip, last_updated
FROM dbo.blacklisted_ip;
END;
GO