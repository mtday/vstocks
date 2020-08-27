
DROP TABLE IF EXISTS flyway_schema_history;

-- system metric tables

DROP TABLE IF EXISTS active_transaction_counts;
DROP TABLE IF EXISTS total_transaction_counts;
DROP TABLE IF EXISTS total_user_counts;
DROP TABLE IF EXISTS active_user_counts;

-- portfolio tables

DROP TABLE IF EXISTS total_values;
DROP TABLE IF EXISTS total_ranks;
DROP TABLE IF EXISTS market_total_values;
DROP TABLE IF EXISTS market_total_ranks;
DROP TABLE IF EXISTS market_values;
DROP TABLE IF EXISTS market_ranks;
DROP TABLE IF EXISTS credit_values;
DROP TABLE IF EXISTS credit_ranks;

-- data tables

DROP TABLE IF EXISTS user_achievements;
DROP TABLE IF EXISTS user_stocks;
DROP TABLE IF EXISTS user_credits;
DROP TABLE IF EXISTS activity_logs;
DROP TABLE IF EXISTS stock_prices;
DROP TABLE IF EXISTS stocks;
DROP TABLE IF EXISTS users;

