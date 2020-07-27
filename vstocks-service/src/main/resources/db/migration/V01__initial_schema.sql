

CREATE TABLE users (
    id                 VARCHAR(80)  NOT NULL,
    username           VARCHAR(500) NOT NULL,
    source             VARCHAR(30)  NOT NULL,
    display_name       VARCHAR(30)  NOT NULL,

    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_unique_username_source UNIQUE (username, source)
);

CREATE INDEX idx_users_id ON users (id);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_source ON users (source);


CREATE TABLE markets (
    id                 VARCHAR(80)  NOT NULL,
    name               VARCHAR(100) NOT NULL,

    CONSTRAINT markets_pk PRIMARY KEY (id)
);

CREATE INDEX idx_markets_id ON markets (id);
CREATE INDEX idx_markets_name ON markets (name);


CREATE TABLE stocks (
    id                 VARCHAR(80)  NOT NULL,
    market_id          VARCHAR(80)  NOT NULL,
    symbol             VARCHAR(100) NOT NULL,
    name               VARCHAR(100) NOT NULL,

    CONSTRAINT stocks_pk PRIMARY KEY (id),
    CONSTRAINT stocks_fk_market_id FOREIGN KEY (market_id)
        REFERENCES markets (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_stocks_id ON stocks (id);
CREATE INDEX idx_stocks_market_id ON stocks (market_id);
CREATE INDEX idx_stocks_symbol ON stocks (symbol);
CREATE INDEX idx_stocks_name ON stocks (name);


CREATE TABLE stock_prices (
    id                 VARCHAR(80)  NOT NULL,
    market_id          VARCHAR(80)  NOT NULL,
    stock_id           VARCHAR(80)  NOT NULL,
    timestamp          TIMESTAMP    NOT NULL,
    price              INTEGER      NOT NULL,

    CONSTRAINT stock_prices_pk PRIMARY KEY (id),
    CONSTRAINT stock_prices_unique UNIQUE (market_id, stock_id, timestamp),
    CONSTRAINT stock_prices_fk_market_id FOREIGN KEY (market_id)
        REFERENCES markets (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT stock_prices_fk_stock_id FOREIGN KEY (stock_id)
        REFERENCES stocks (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_stock_prices_id ON stock_prices (id);
CREATE INDEX idx_stock_prices_market_id ON stock_prices (market_id);
CREATE INDEX idx_stock_prices_stock_id ON stock_prices (stock_id);
CREATE INDEX idx_stock_prices_timestamp ON stock_prices (timestamp);


CREATE TABLE activity_logs (
    id                 VARCHAR(80)  NOT NULL,
    user_id            VARCHAR(80)  NOT NULL,
    market_id          VARCHAR(80)  NOT NULL,
    stock_id           VARCHAR(80)  NOT NULL,
    timestamp          TIMESTAMP    NOT NULL,
    shares             INTEGER      NOT NULL,
    price              INTEGER      NOT NULL,

    CONSTRAINT activity_logs_pk PRIMARY KEY (id),
    CONSTRAINT activity_logs_unique UNIQUE (user_id, market_id, stock_id, timestamp),
    CONSTRAINT activity_logs_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT activity_logs_fk_market_id FOREIGN KEY (market_id)
        REFERENCES markets (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT activity_logs_fk_stock_id FOREIGN KEY (stock_id)
        REFERENCES stocks (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_activity_logs_id ON activity_logs (id);
CREATE INDEX idx_activity_logs_user_id ON activity_logs (user_id);
CREATE INDEX idx_activity_logs_market_id ON activity_logs (market_id);
CREATE INDEX idx_activity_logs_stock_id ON activity_logs (stock_id);
CREATE INDEX idx_activity_logs_timestamp ON activity_logs (timestamp);


CREATE TABLE user_balances (
    user_id            VARCHAR(80)  NOT NULL,
    balance            INTEGER      NOT NULL,

    CONSTRAINT user_balances_pk PRIMARY KEY (user_id),
    CONSTRAINT user_balances_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_user_balances_user_id ON user_balances (user_id);


CREATE TABLE user_stocks (
    user_id            VARCHAR(80)  NOT NULL,
    market_id          VARCHAR(80)  NOT NULL,
    stock_id           VARCHAR(80)  NOT NULL,
    shares             INTEGER      NOT NULL,

    CONSTRAINT user_stocks_pk PRIMARY KEY (user_id, market_id, stock_id),
    CONSTRAINT user_stocks_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT user_stocks_fk_market_id FOREIGN KEY (market_id)
        REFERENCES markets (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT user_stocks_fk_stock_id FOREIGN KEY (stock_id)
        REFERENCES stocks (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_user_stocks_user_id ON user_stocks (user_id);
CREATE INDEX idx_user_stocks_market_id ON user_stocks (market_id);
CREATE INDEX idx_user_stocks_stock_id ON user_stocks (stock_id);
