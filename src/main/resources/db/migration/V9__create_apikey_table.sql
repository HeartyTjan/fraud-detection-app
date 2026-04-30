CREATE TABLE api_key (
     id          BIGINT IDENTITY(1,1)      NOT NULL PRIMARY KEY,
     created_at  DATETIME2                 NOT NULL,
     updated_at  DATETIME2                 NOT NULL,
     created_by  VARCHAR(255)              NULL,
     updated_by  VARCHAR(255)              NULL,
     deleted     BIT                       NOT NULL,
    [key]       VARCHAR(255)              NULL,  -- key is a reserved word, so use brackets
    prefix      VARCHAR(255)              NULL,
    active      BIT                       NOT NULL,
    owner_name  VARCHAR(255)              NULL
    );