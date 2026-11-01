CREATE TABLE monitored_service
(
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100)  NOT NULL,
    url             VARCHAR(2048) NOT NULL,
    last_status     VARCHAR(16)   NOT NULL DEFAULT 'UNKNOWN',
    last_checked_at DATETIME(6)   NULL,
    CONSTRAINT pk_monitored_service PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
