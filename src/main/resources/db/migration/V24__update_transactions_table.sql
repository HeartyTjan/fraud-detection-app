
IF EXISTS (
    SELECT 1 FROM sys.default_constraints
    WHERE parent_object_id = OBJECT_ID('transactions')
    AND COL_NAME(parent_object_id, parent_column_id) = 'updated_at'
)
BEGIN
    DECLARE @constraintName NVARCHAR(200);

SELECT @constraintName = name
FROM sys.default_constraints
WHERE parent_object_id = OBJECT_ID('transactions')
  AND COL_NAME(parent_object_id, parent_column_id) = 'updated_at';

EXEC('ALTER TABLE transactions DROP CONSTRAINT ' + @constraintName);
END;

ALTER TABLE transactions
    ADD CONSTRAINT DF_updated_at DEFAULT GETDATE() FOR updated_at;


IF EXISTS (
    SELECT 1 FROM sys.default_constraints
    WHERE parent_object_id = OBJECT_ID('transactions')
    AND COL_NAME(parent_object_id, parent_column_id) = 'deleted'
)
BEGIN
    DECLARE @constraintNameDeleted NVARCHAR(200);

SELECT @constraintNameDeleted = name
FROM sys.default_constraints
WHERE parent_object_id = OBJECT_ID('transactions')
  AND COL_NAME(parent_object_id, parent_column_id) = 'deleted';

EXEC('ALTER TABLE transactions DROP CONSTRAINT ' + @constraintNameDeleted);
END;

ALTER TABLE transactions
    ADD CONSTRAINT DF_deleted DEFAULT 0 FOR deleted;