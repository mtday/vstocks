
INSERT INTO users (id, email, username, display_name, profile_image) VALUES
('user1',                                'user1@domain.com',    'user1',     'User 1',   NULL),
('user2',                                'user2@domain.com',    'user2',     'User 2',   NULL),
('user3',                                'user3@domain.com',    'user3',     'User 3',   NULL),
('user4',                                'user4@domain.com',    'user4',     'User 4',   NULL),
('055a794f-257e-3a48-b334-63ab626b9bc0', 'mikeday99@gmail.com', 'mikeday99', 'Mike Day', 'https://pbs.twimg.com/profile_images/1771115658/Bass_Fish_by_ovalbrush_normal.jpg')
ON CONFLICT DO NOTHING;


INSERT INTO user_credits (user_id, credits) VALUES
('user1', 10000),
('user2', 10000),
('user3', 10000),
('user4', 10000),
('055a794f-257e-3a48-b334-63ab626b9bc0', 10000)
ON CONFLICT DO NOTHING;


INSERT INTO stocks (market, symbol, name) VALUES
('TWITTER', 'realDonaldTrump', 'Donald J. Trump'),
('TWITTER', 'POTUS',           'President Trump'),
('TWITTER', 'WhiteHouse',      'The White House'),
('YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 'Tim Pool'),
('YOUTUBE', 'UCLwNTXWEjVd2qIHLcXxQWxA', 'Timcast IRL')
ON CONFLICT DO NOTHING;


INSERT INTO stock_prices (market, symbol, timestamp, price) VALUES
('TWITTER', 'realDonaldTrump', '2020-01-01 00:00:00', 4867),
('TWITTER', 'realDonaldTrump', '2020-02-01 00:00:00', 4878),
('TWITTER', 'realDonaldTrump', '2020-03-01 00:00:00', 4883),
('TWITTER', 'POTUS', '2020-01-01 00:00:00', 2678),
('TWITTER', 'POTUS', '2020-02-01 00:00:00', 2689),
('TWITTER', 'POTUS', '2020-03-01 00:00:00', 2693),
('TWITTER', 'WhiteHouse', '2020-01-01 00:00:00', 2178),
('TWITTER', 'WhiteHouse', '2020-02-01 00:00:00', 2189),
('TWITTER', 'WhiteHouse', '2020-03-01 00:00:00', 2179),
('YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', '2020-01-01 00:00:00', 1430),
('YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', '2020-02-01 00:00:00', 1468),
('YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', '2020-03-01 00:00:00', 1502),
('YOUTUBE', 'UCLwNTXWEjVd2qIHLcXxQWxA', '2020-01-01 00:00:00', 1230),
('YOUTUBE', 'UCLwNTXWEjVd2qIHLcXxQWxA', '2020-02-01 00:00:00', 1268),
('YOUTUBE', 'UCLwNTXWEjVd2qIHLcXxQWxA', '2020-03-01 00:00:00', 1302)
ON CONFLICT DO NOTHING;


INSERT INTO user_stocks (user_id, market, symbol, shares) VALUES
('user1',                                'TWITTER', 'realDonaldTrump',          2),
('user1',                                'TWITTER', 'POTUS',                    1),
('user1',                                'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 3),
('user2',                                'TWITTER', 'WhiteHouse',               2),
('user3',                                'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 5),
('055a794f-257e-3a48-b334-63ab626b9bc0', 'TWITTER', 'realDonaldTrump',          5),
('055a794f-257e-3a48-b334-63ab626b9bc0', 'TWITTER', 'POTUS',                    5),
('055a794f-257e-3a48-b334-63ab626b9bc0', 'TWITTER', 'WhiteHouse',               5),
('055a794f-257e-3a48-b334-63ab626b9bc0', 'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 5),
('055a794f-257e-3a48-b334-63ab626b9bc0', 'YOUTUBE', 'UCLwNTXWEjVd2qIHLcXxQWxA', 5)
ON CONFLICT DO NOTHING;


INSERT INTO activity_logs (id, user_id, type, timestamp, market, symbol, shares, price) VALUES
('1',  'user1',                                'STOCK_BUY',  '2020-01-01 00:00:00', 'TWITTER', 'realDonaldTrump',          5,    4867),
('2',  'user2',                                'STOCK_BUY',  '2020-01-01 00:00:00', 'TWITTER', 'realDonaldTrump',          1,    4867),
('3',  'user2',                                'STOCK_BUY',  '2020-01-01 00:00:00', 'TWITTER', 'POTUS',                    1,    2678),
('4',  'user2',                                'STOCK_SELL', '2020-02-01 00:00:00', 'TWITTER', 'POTUS',                    1,    2693),
('5',  'user1',                                'STOCK_BUY',  '2020-01-01 00:00:00', 'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 1,    1430),
('6',  'user1',                                'STOCK_BUY',  '2020-02-01 00:00:00', 'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 1,    1468),
('7',  'user1',                                'USER_LOGIN', '2020-01-01 00:00:00', NULL,      NULL,                       NULL, NULL),
('8',  'user2',                                'USER_LOGIN', '2020-02-01 00:00:00', NULL,      NULL,                       NULL, NULL),
('9',  '055a794f-257e-3a48-b334-63ab626b9bc0', 'USER_LOGIN', '2020-08-27 03:04:10', NULL,      NULL,                       NULL, NULL),
('10', '055a794f-257e-3a48-b334-63ab626b9bc0', 'STOCK_BUY',  '2020-08-27 03:04:15', 'TWITTER', 'realDonaldTrump',          5,    4883),
('11', '055a794f-257e-3a48-b334-63ab626b9bc0', 'STOCK_BUY',  '2020-08-27 03:04:15', 'TWITTER', 'POTUS',                    5,    2693),
('12', '055a794f-257e-3a48-b334-63ab626b9bc0', 'STOCK_BUY',  '2020-08-27 03:04:16', 'TWITTER', 'WhiteHouse',               5,    2179),
('13', '055a794f-257e-3a48-b334-63ab626b9bc0', 'STOCK_BUY',  '2020-08-27 03:04:16', 'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 5,    1502),
('14', '055a794f-257e-3a48-b334-63ab626b9bc0', 'STOCK_BUY',  '2020-08-27 03:04:17', 'YOUTUBE', 'UCLwNTXWEjVd2qIHLcXxQWxA', 5,    1302)
ON CONFLICT DO NOTHING;


INSERT INTO credit_ranks (batch, user_id, timestamp, rank, value) VALUES
(4, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 03:00:00', 1, 11000),
(4, 'user1',                                '2020-08-21 03:00:00', 2, 10000),
(4, 'user2',                                '2020-08-21 03:00:00', 2, 10000),
(4, 'user3',                                '2020-08-21 03:00:00', 2, 10000),
(4, 'user4',                                '2020-08-21 03:00:00', 2, 10000),
(3, 'user1',                                '2020-08-21 02:00:00', 1, 10000),
(3, 'user2',                                '2020-08-21 02:00:00', 1, 10000),
(3, 'user3',                                '2020-08-21 02:00:00', 1, 10000),
(3, 'user4',                                '2020-08-21 02:00:00', 1, 10000),
(3, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 02:00:00', 2, 9000),
(2, 'user1',                                '2020-08-21 01:00:00', 1, 10000),
(2, 'user2',                                '2020-08-21 01:00:00', 1, 10000),
(2, 'user3',                                '2020-08-21 01:00:00', 1, 10000),
(2, 'user4',                                '2020-08-21 01:00:00', 1, 10000),
(2, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 01:00:00', 2, 9000),
(1, 'user1',                                '2020-08-21 00:00:00', 1, 10000),
(1, 'user2',                                '2020-08-21 00:00:00', 1, 10000),
(1, 'user3',                                '2020-08-21 00:00:00', 1, 10000),
(1, 'user4',                                '2020-08-21 00:00:00', 1, 10000)
ON CONFLICT DO NOTHING;


INSERT INTO market_ranks (batch, user_id, market, timestamp, rank, value) VALUES
(4, '055a794f-257e-3a48-b334-63ab626b9bc0', 'TWITTER', '2020-08-21 03:00:00', 1, 11000),
(4, 'user1',                                'TWITTER', '2020-08-21 03:00:00', 2, 10000),
(4, 'user2',                                'TWITTER', '2020-08-21 03:00:00', 2, 10000),
(4, 'user3',                                'TWITTER', '2020-08-21 03:00:00', 2, 10000),
(4, 'user4',                                'TWITTER', '2020-08-21 03:00:00', 2, 10000),
(3, 'user1',                                'TWITTER', '2020-08-21 02:00:00', 1, 10000),
(3, 'user2',                                'TWITTER', '2020-08-21 02:00:00', 1, 10000),
(3, 'user3',                                'TWITTER', '2020-08-21 02:00:00', 1, 10000),
(3, 'user4',                                'TWITTER', '2020-08-21 02:00:00', 1, 10000),
(3, '055a794f-257e-3a48-b334-63ab626b9bc0', 'TWITTER', '2020-08-21 02:00:00', 2, 9000),
(2, 'user1',                                'TWITTER', '2020-08-21 01:00:00', 1, 10000),
(2, 'user2',                                'TWITTER', '2020-08-21 01:00:00', 1, 10000),
(2, 'user3',                                'TWITTER', '2020-08-21 01:00:00', 1, 10000),
(2, 'user4',                                'TWITTER', '2020-08-21 01:00:00', 1, 10000),
(2, '055a794f-257e-3a48-b334-63ab626b9bc0', 'TWITTER', '2020-08-21 01:00:00', 2, 9000),
(1, 'user1',                                'TWITTER', '2020-08-21 00:00:00', 1, 10000),
(1, 'user2',                                'TWITTER', '2020-08-21 00:00:00', 1, 10000),
(1, 'user3',                                'TWITTER', '2020-08-21 00:00:00', 1, 10000),
(1, 'user4',                                'TWITTER', '2020-08-21 00:00:00', 1, 10000)
ON CONFLICT DO NOTHING;


INSERT INTO market_total_ranks (batch, user_id, timestamp, rank, value) VALUES
(4, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 03:00:00', 1, 11000),
(4, 'user1',                                '2020-08-21 03:00:00', 2, 10000),
(4, 'user2',                                '2020-08-21 03:00:00', 2, 10000),
(4, 'user3',                                '2020-08-21 03:00:00', 2, 10000),
(4, 'user4',                                '2020-08-21 03:00:00', 2, 10000),
(3, 'user1',                                '2020-08-21 02:00:00', 1, 10000),
(3, 'user2',                                '2020-08-21 02:00:00', 1, 10000),
(3, 'user3',                                '2020-08-21 02:00:00', 1, 10000),
(3, 'user4',                                '2020-08-21 02:00:00', 1, 10000),
(3, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 02:00:00', 2, 9000),
(2, 'user1',                                '2020-08-21 01:00:00', 1, 10000),
(2, 'user2',                                '2020-08-21 01:00:00', 1, 10000),
(2, 'user3',                                '2020-08-21 01:00:00', 1, 10000),
(2, 'user4',                                '2020-08-21 01:00:00', 1, 10000),
(2, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 01:00:00', 2, 9000),
(1, 'user1',                                '2020-08-21 00:00:00', 1, 10000),
(1, 'user2',                                '2020-08-21 00:00:00', 1, 10000),
(1, 'user3',                                '2020-08-21 00:00:00', 1, 10000),
(1, 'user4',                                '2020-08-21 00:00:00', 1, 10000)
ON CONFLICT DO NOTHING;


INSERT INTO total_ranks (batch, user_id, timestamp, rank, value) VALUES
(4, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 03:00:00', 1, 21000),
(4, 'user1',                                '2020-08-21 03:00:00', 2, 20000),
(4, 'user2',                                '2020-08-21 03:00:00', 2, 20000),
(4, 'user3',                                '2020-08-21 03:00:00', 2, 20000),
(4, 'user4',                                '2020-08-21 03:00:00', 2, 20000),
(3, 'user1',                                '2020-08-21 02:00:00', 1, 20000),
(3, 'user2',                                '2020-08-21 02:00:00', 1, 20000),
(3, 'user3',                                '2020-08-21 02:00:00', 1, 20000),
(3, 'user4',                                '2020-08-21 02:00:00', 1, 20000),
(3, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 02:00:00', 2, 19000),
(2, 'user1',                                '2020-08-21 01:00:00', 1, 20000),
(2, 'user2',                                '2020-08-21 01:00:00', 1, 20000),
(2, 'user3',                                '2020-08-21 01:00:00', 1, 20000),
(2, 'user4',                                '2020-08-21 01:00:00', 1, 20000),
(2, '055a794f-257e-3a48-b334-63ab626b9bc0', '2020-08-21 01:00:00', 2, 19000),
(1, 'user1',                                '2020-08-21 00:00:00', 1, 20000),
(1, 'user2',                                '2020-08-21 00:00:00', 1, 20000),
(1, 'user3',                                '2020-08-21 00:00:00', 1, 20000),
(1, 'user4',                                '2020-08-21 00:00:00', 1, 20000)
ON CONFLICT DO NOTHING;


INSERT INTO overall_credit_values (timestamp, value) VALUES
('2020-08-21 00:00:00', 40000),
('2020-08-21 01:00:00', 40000),
('2020-08-21 02:00:00', 50000),
('2020-08-21 03:00:00', 50000),
('2020-08-21 04:00:00', 50000)
ON CONFLICT DO NOTHING;


INSERT INTO overall_market_total_values (timestamp, value) VALUES
('2020-08-21 00:00:00', 25373),
('2020-08-21 01:00:00', 25373),
('2020-08-21 02:00:00', 28122),
('2020-08-21 03:00:00', 28122),
('2020-08-21 04:00:00', 31058)
ON CONFLICT DO NOTHING;


INSERT INTO overall_total_values (timestamp, value) VALUES
('2020-08-21 00:00:00', 75373),
('2020-08-21 01:00:00', 75373),
('2020-08-21 02:00:00', 78122),
('2020-08-21 03:00:00', 78122),
('2020-08-21 04:00:00', 81058)
ON CONFLICT DO NOTHING;


INSERT INTO overall_market_values (market, timestamp, value) VALUES
('TWITTER', '2020-08-21 00:00:00', 15373),
('YOUTUBE', '2020-08-21 00:00:00', 5823),
('TWITTER', '2020-08-21 01:00:00', 15373),
('YOUTUBE', '2020-08-21 01:00:00', 5823),
('TWITTER', '2020-08-21 02:00:00', 18122),
('YOUTUBE', '2020-08-21 02:00:00', 8005),
('TWITTER', '2020-08-21 03:00:00', 18122),
('YOUTUBE', '2020-08-21 03:00:00', 8005),
('TWITTER', '2020-08-21 04:00:00', 21058),
('YOUTUBE', '2020-08-21 04:00:00', 9347)
ON CONFLICT DO NOTHING;


INSERT INTO active_transaction_counts (timestamp, count) VALUES
('2020-08-21 00:00:00', 3),
('2020-08-21 01:00:00', 1),
('2020-08-21 02:00:00', 1),
('2020-08-21 03:00:00', 1),
('2020-08-21 04:00:00', 0)
ON CONFLICT DO NOTHING;


INSERT INTO total_transaction_counts (timestamp, count) VALUES
('2020-08-21 00:00:00', 10),
('2020-08-21 01:00:00', 7),
('2020-08-21 02:00:00', 6),
('2020-08-21 03:00:00', 5),
('2020-08-21 04:00:00', 5)
ON CONFLICT DO NOTHING;


INSERT INTO active_user_counts (timestamp, count) VALUES
('2020-08-21 00:00:00', 3),
('2020-08-21 01:00:00', 1),
('2020-08-21 02:00:00', 1),
('2020-08-21 03:00:00', 1),
('2020-08-21 04:00:00', 0)
ON CONFLICT DO NOTHING;


INSERT INTO total_user_counts (timestamp, count) VALUES
('2020-08-21 00:00:00', 10),
('2020-08-21 01:00:00', 7),
('2020-08-21 02:00:00', 6),
('2020-08-21 03:00:00', 5),
('2020-08-21 04:00:00', 5)
ON CONFLICT DO NOTHING;


INSERT INTO user_achievements (user_id, achievement_id, timestamp, description) VALUES
('055a794f-257e-3a48-b334-63ab626b9bc0', 'first_stock_buy_twitter', '2020-08-21 04:00:00',
    'First Twitter stock purchase achieved - 5 shares of realDonaldTrump bought for 10 credits each.'),
('055a794f-257e-3a48-b334-63ab626b9bc0', 'first_stock_buy_youtube', '2020-08-21 04:00:00',
    'First YouTube stock purchase achieved - 5 shares of UCe02lGcO-ahAURWuxAJnjdA bought for 10 credits each.')
ON CONFLICT DO NOTHING;

