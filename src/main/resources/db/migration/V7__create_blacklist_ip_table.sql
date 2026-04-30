CREATE TABLE blacklisted_ip (
    ip           VARCHAR(255) NOT NULL,
    last_updated DATETIME2    NULL,
    CONSTRAINT pk_blacklistedip PRIMARY KEY (ip)
);