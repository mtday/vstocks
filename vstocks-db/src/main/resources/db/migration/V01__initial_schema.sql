

CREATE TABLE users (
    id                 TEXT NOT NULL,
    email              TEXT NOT NULL,
    username           TEXT NOT NULL,
    display_name       TEXT NOT NULL,
    profile_image      TEXT,

    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_unique_email UNIQUE (email),
    CONSTRAINT users_unique_username UNIQUE (username)
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_display_name ON users (display_name);
CREATE INDEX idx_users_profile_image ON users (profile_image);


CREATE TABLE stocks (
    market             TEXT    NOT NULL,
    symbol             TEXT    NOT NULL,
    name               TEXT    NOT NULL,
    profile_image      TEXT,

    CONSTRAINT stocks_pk PRIMARY KEY (market, symbol)
);

CREATE INDEX idx_stocks_market ON stocks (market);
CREATE INDEX idx_stocks_symbol ON stocks (symbol);
CREATE INDEX idx_stocks_name ON stocks (name);
CREATE INDEX idx_stocks_profile_image ON stocks (profile_image);


CREATE TABLE stock_prices (
    market             TEXT         NOT NULL,
    symbol             TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    price              BIGINT       NOT NULL,

    CONSTRAINT stock_prices_pk PRIMARY KEY (market, symbol, timestamp),
    CONSTRAINT stock_prices_fk_stock FOREIGN KEY (market, symbol)
        REFERENCES stocks (market, symbol) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_stock_prices_market ON stock_prices (market);
CREATE INDEX idx_stock_prices_symbol ON stock_prices (symbol);
CREATE INDEX idx_stock_prices_timestamp ON stock_prices (timestamp);


CREATE TABLE activity_logs (
    id                 TEXT         NOT NULL,
    user_id            TEXT         NOT NULL,
    type               TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    market             TEXT,
    symbol             TEXT,
    shares             INTEGER,
    price              BIGINT,

    CONSTRAINT activity_logs_pk PRIMARY KEY (id),
    CONSTRAINT activity_logs_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT activity_logs_fk_stock FOREIGN KEY (market, symbol)
        REFERENCES stocks (market, symbol) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_activity_logs_id ON activity_logs (id);
CREATE INDEX idx_activity_logs_user_id ON activity_logs (user_id);
CREATE INDEX idx_activity_logs_type ON activity_logs (type);
CREATE INDEX idx_activity_logs_market ON activity_logs (market);
CREATE INDEX idx_activity_logs_symbol ON activity_logs (symbol);
CREATE INDEX idx_activity_logs_timestamp ON activity_logs (timestamp);


CREATE TABLE user_credits (
    user_id            TEXT    NOT NULL,
    credits            BIGINT  NOT NULL,

    CONSTRAINT user_credits_pk PRIMARY KEY (user_id),
    CONSTRAINT user_credits_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_user_credits_user_id ON user_credits (user_id);
CREATE INDEX idx_user_credits_credits ON user_credits (credits);


CREATE TABLE user_stocks (
    user_id            TEXT    NOT NULL,
    market             TEXT    NOT NULL,
    symbol             TEXT    NOT NULL,
    shares             INTEGER NOT NULL,

    CONSTRAINT user_stocks_pk PRIMARY KEY (user_id, market, symbol),
    CONSTRAINT user_stocks_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT user_stocks_fk_stock FOREIGN KEY (market, symbol)
        REFERENCES stocks (market, symbol) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_user_stocks_user_id ON user_stocks (user_id);
CREATE INDEX idx_user_stocks_market ON user_stocks (market);
CREATE INDEX idx_user_stocks_symbol ON user_stocks (symbol);
CREATE INDEX idx_user_stocks_shares ON user_stocks (shares);


CREATE TABLE user_achievements (
    user_id            TEXT         NOT NULL,
    achievement_id     TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    description        TEXT         NOT NULL,

    CONSTRAINT user_achievements_pk PRIMARY KEY (user_id, achievement_id),
    CONSTRAINT user_achievements_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_user_achievements_user_id ON user_achievements (user_id);
CREATE INDEX idx_user_achievements_achievement_id ON user_achievements (achievement_id);
CREATE INDEX idx_user_achievements_timestamp ON user_achievements (timestamp);


-- portfolio tables

CREATE SEQUENCE credit_ranks_batch_sequence;
CREATE TABLE credit_ranks (
    batch              BIGINT       NOT NULL,
    user_id            TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    rank               BIGINT       NOT NULL,
    value              BIGINT       NOT NULL,

    CONSTRAINT credit_ranks_pk PRIMARY KEY (batch, user_id),
    CONSTRAINT credit_ranks_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_credit_ranks_batch ON credit_ranks (batch);
CREATE INDEX idx_credit_ranks_user_id ON credit_ranks (user_id);
CREATE INDEX idx_credit_ranks_timestamp ON credit_ranks (timestamp);
CREATE INDEX idx_credit_ranks_rank ON credit_ranks (rank);
CREATE INDEX idx_credit_ranks_value ON credit_ranks (value);


CREATE SEQUENCE market_ranks_batch_sequence;
CREATE TABLE market_ranks (
    batch              BIGINT       NOT NULL,
    user_id            TEXT         NOT NULL,
    market             TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    rank               BIGINT       NOT NULL,
    value              BIGINT       NOT NULL,

    CONSTRAINT market_ranks_pk PRIMARY KEY (batch, user_id, market),
    CONSTRAINT market_ranks_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_market_ranks_batch ON market_ranks (batch);
CREATE INDEX idx_market_ranks_user_id ON market_ranks (user_id);
CREATE INDEX idx_market_ranks_market ON market_ranks (market);
CREATE INDEX idx_market_ranks_timestamp ON market_ranks (timestamp);
CREATE INDEX idx_market_ranks_rank ON market_ranks (rank);
CREATE INDEX idx_market_ranks_value ON market_ranks (value);


CREATE SEQUENCE market_total_ranks_batch_sequence;
CREATE TABLE market_total_ranks (
    batch              BIGINT       NOT NULL,
    user_id            TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    rank               BIGINT       NOT NULL,
    value              BIGINT       NOT NULL,

    CONSTRAINT market_total_ranks_pk PRIMARY KEY (batch, user_id),
    CONSTRAINT market_total_ranks_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_market_total_ranks_batch ON market_total_ranks (batch);
CREATE INDEX idx_market_total_ranks_user_id ON market_total_ranks (user_id);
CREATE INDEX idx_market_total_ranks_timestamp ON market_total_ranks (timestamp);
CREATE INDEX idx_market_total_ranks_rank ON market_total_ranks (rank);
CREATE INDEX idx_market_total_ranks_value ON market_total_ranks (value);


CREATE SEQUENCE total_ranks_batch_sequence;
CREATE TABLE total_ranks (
    batch              BIGINT       NOT NULL,
    user_id            TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    rank               BIGINT       NOT NULL,
    value              BIGINT       NOT NULL,

    CONSTRAINT total_ranks_pk PRIMARY KEY (batch, user_id),
    CONSTRAINT total_ranks_fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_total_ranks_batch ON total_ranks (batch);
CREATE INDEX idx_total_ranks_user_id ON total_ranks (user_id);
CREATE INDEX idx_total_ranks_timestamp ON total_ranks (timestamp);
CREATE INDEX idx_total_ranks_rank ON total_ranks (rank);
CREATE INDEX idx_total_ranks_value ON total_ranks (value);


-- system metric tables

CREATE TABLE overall_credit_values (
    timestamp          TIMESTAMP(0) NOT NULL,
    value              BIGINT       NOT NULL,

    CONSTRAINT overall_credit_values_pk PRIMARY KEY (timestamp)
);

CREATE INDEX idx_overall_credit_values_timestamp ON overall_credit_values (timestamp);
CREATE INDEX idx_overall_credit_values_value ON overall_credit_values (value);

CREATE TABLE overall_market_values (
    market             TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    value              BIGINT       NOT NULL,

    CONSTRAINT overall_market_values_pk PRIMARY KEY (market, timestamp)
);

CREATE INDEX idx_overall_market_values_market ON overall_market_values (market);
CREATE INDEX idx_overall_market_values_timestamp ON overall_market_values (timestamp);
CREATE INDEX idx_overall_market_values_value ON overall_market_values (value);

CREATE TABLE overall_market_total_values (
    timestamp          TIMESTAMP(0) NOT NULL,
    value              BIGINT       NOT NULL,

    CONSTRAINT overall_market_total_values_pk PRIMARY KEY (timestamp)
);

CREATE INDEX idx_overall_market_total_values_timestamp ON overall_market_total_values (timestamp);
CREATE INDEX idx_overall_market_total_values_value ON overall_market_total_values (value);

CREATE TABLE overall_total_values (
    timestamp          TIMESTAMP(0) NOT NULL,
    value              BIGINT       NOT NULL,

    CONSTRAINT overall_total_values_pk PRIMARY KEY (timestamp)
);

CREATE INDEX idx_overall_total_values_timestamp ON overall_total_values (timestamp);
CREATE INDEX idx_overall_total_values_value ON overall_total_values (value);

CREATE TABLE active_user_counts (
    timestamp          TIMESTAMP(0) NOT NULL,
    count              BIGINT       NOT NULL,

    CONSTRAINT active_user_counts_pk PRIMARY KEY (timestamp)
);

CREATE INDEX idx_active_user_counts_timestamp ON active_user_counts (timestamp);
CREATE INDEX idx_active_user_counts_count ON active_user_counts (count);


CREATE TABLE total_user_counts (
    timestamp          TIMESTAMP(0) NOT NULL,
    count              BIGINT       NOT NULL,

    CONSTRAINT total_user_counts_pk PRIMARY KEY (timestamp)
);

CREATE INDEX idx_total_user_counts_timestamp ON total_user_counts (timestamp);
CREATE INDEX idx_total_user_counts_count ON total_user_counts (count);


CREATE TABLE market_transaction_counts (
    market             TEXT         NOT NULL,
    timestamp          TIMESTAMP(0) NOT NULL,
    count              BIGINT       NOT NULL,

    CONSTRAINT market_transaction_counts_pk PRIMARY KEY (market, timestamp)
);

CREATE INDEX idx_market_transaction_counts_market ON market_transaction_counts (market);
CREATE INDEX idx_market_transaction_counts_timestamp ON market_transaction_counts (timestamp);
CREATE INDEX idx_market_transaction_counts_count ON market_transaction_counts (count);


CREATE TABLE active_transaction_counts (
    timestamp          TIMESTAMP(0) NOT NULL,
    count              BIGINT       NOT NULL,

    CONSTRAINT active_transaction_counts_pk PRIMARY KEY (timestamp)
);

CREATE INDEX idx_active_transaction_counts_timestamp ON active_transaction_counts (timestamp);
CREATE INDEX idx_active_transaction_counts_count ON active_transaction_counts (count);


CREATE TABLE total_transaction_counts (
    timestamp          TIMESTAMP(0) NOT NULL,
    count              BIGINT       NOT NULL,

    CONSTRAINT total_transaction_counts_pk PRIMARY KEY (timestamp)
);

CREATE INDEX idx_total_transaction_counts_timestamp ON total_transaction_counts (timestamp);
CREATE INDEX idx_total_transaction_counts_count ON total_transaction_counts (count);

