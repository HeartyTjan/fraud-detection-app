ALTER TABLE api_key ADD key_hash VARCHAR(64) NULL;
GO

CREATE UNIQUE INDEX idx_apikey_keyhash ON api_key(key_hash) WHERE key_hash IS NOT NULL;