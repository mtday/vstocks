

CREATE TABLE exchanges (
    id                 VARCHAR(36) NOT NULL,
    name               VARCHAR(100) NOT NULL,

    CONSTRAINT exchanges_pk PRIMARY KEY (id)
);


CREATE TABLE symbols (
    exchange_id        VARCHAR(36) NOT NULL,
    id                 VARCHAR(36) NOT NULL,
    symbol             VARCHAR(100) NOT NULL,
    name               VARCHAR(100) NOT NULL,

    CONSTRAINT symbols_pk PRIMARY KEY (id),
    CONSTRAINT symbols_fk_exchange_id FOREIGN KEY (exchange_id)
        REFERENCES exchanges (id) ON UPDATE CASCADE ON DELETE CASCADE
);
