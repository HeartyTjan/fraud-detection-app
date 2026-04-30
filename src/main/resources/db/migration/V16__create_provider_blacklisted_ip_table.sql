CREATE TABLE provider_blacklistedip (
    ip               VARCHAR(255) NOT NULL,
    confidence_level INT          NOT NULL,
    last_seen        BIGINT       NOT NULL,
    sessions         INT          NOT NULL,
    protocols        NVARCHAR(MAX) NULL,  -- see note below
    country_code     VARCHAR(255) NULL,
    CONSTRAINT pk_providerblacklistedip PRIMARY KEY (ip)
);