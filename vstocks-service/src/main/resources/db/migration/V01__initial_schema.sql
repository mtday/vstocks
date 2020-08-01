

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


CREATE TABLE stocks (
    market             VARCHAR(80)  NOT NULL,
    symbol             VARCHAR(100) NOT NULL,
    name               VARCHAR(100) NOT NULL,

    CONSTRAINT stocks_pk PRIMARY KEY (market, symbol)
);

CREATE INDEX idx_stocks_market ON stocks (market);
CREATE INDEX idx_stocks_symbol ON stocks (symbol);
CREATE INDEX idx_stocks_name ON stocks (name);


CREATE TABLE stock_prices (
    market             VARCHAR(80)  NOT NULL,
    symbol             VARCHAR(80)  NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    price              INTEGER      NOT NULL,

    CONSTRAINT stock_prices_pk PRIMARY KEY (market, symbol, timestamp),
    CONSTRAINT stock_prices_fk_stock FOREIGN KEY (market, symbol)
        REFERENCES stocks (market, symbol) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_stock_prices_market ON stock_prices (market);
CREATE INDEX idx_stock_prices_symbol ON stock_prices (symbol);
CREATE INDEX idx_stock_prices_timestamp ON stock_prices (timestamp);


CREATE TABLE activity_logs (
    id                 VARCHAR(80)  NOT NULL,
    user_id            VARCHAR(80)  NOT NULL,
    market             VARCHAR(80)  NOT NULL,
    symbol             VARCHAR(80)  NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    shares             INTEGER      NOT NULL,
    price              INTEGER      NOT NULL,

    CONSTRAINT activity_logs_pk PRIMARY KEY (id),
    CONSTRAINT activity_logs_unique UNIQUE (user_id, market, symbol, timestamp),
    CONSTRAINT activity_logs_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT activity_logs_fk_stock FOREIGN KEY (market, symbol)
        REFERENCES stocks (market, symbol) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_activity_logs_id ON activity_logs (id);
CREATE INDEX idx_activity_logs_user_id ON activity_logs (user_id);
CREATE INDEX idx_activity_logs_market ON activity_logs (market);
CREATE INDEX idx_activity_logs_symbol ON activity_logs (symbol);
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
    market             VARCHAR(80)  NOT NULL,
    symbol             VARCHAR(80)  NOT NULL,
    shares             INTEGER      NOT NULL,

    CONSTRAINT user_stocks_pk PRIMARY KEY (user_id, market, symbol),
    CONSTRAINT user_stocks_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT user_stocks_fk_symbol FOREIGN KEY (market, symbol)
        REFERENCES stocks (market, symbol) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_user_stocks_user_id ON user_stocks (user_id);
CREATE INDEX idx_user_stocks_market ON user_stocks (market);
CREATE INDEX idx_user_stocks_symbol ON user_stocks (symbol);
