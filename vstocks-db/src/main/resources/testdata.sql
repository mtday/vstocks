
INSERT INTO users (id, email, username, display_name) VALUES
('user1', 'user1@domain.com', 'user1', 'User 1'),
('user2', 'user2@domain.com', 'user2', 'User 2'),
('user3', 'user3@domain.com', 'user3', 'User 3'),
('user4', 'user4@domain.com', 'user4', 'User 4')
ON CONFLICT DO NOTHING;


INSERT INTO user_credits (user_id, credits) VALUES
('user1', 10000),
('user2', 10000),
('user3', 10000),
('user4', 10000)
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
('user1', 'TWITTER', 'realDonaldTrump', 2),
('user1', 'TWITTER', 'POTUS', 1),
('user1', 'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 3),
('user2', 'TWITTER', 'WhiteHouse', 2),
('user3', 'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 5)
ON CONFLICT DO NOTHING;


INSERT INTO activity_logs (id, user_id, type, timestamp, market, symbol, shares, price) VALUES
('1', 'user1', 'STOCK_BUY', '2020-01-01 00:00:00', 'TWITTER', 'realDonaldTrump', 5, 4867),
('2', 'user2', 'STOCK_BUY', '2020-01-01 00:00:00', 'TWITTER', 'realDonaldTrump', 1, 4867),
('3', 'user2', 'STOCK_BUY', '2020-01-01 00:00:00', 'TWITTER', 'POTUS', 1, 2678),
('4', 'user2', 'STOCK_SELL', '2020-02-01 00:00:00', 'TWITTER', 'POTUS', 1, 2693),
('5', 'user1', 'STOCK_BUY', '2020-01-01 00:00:00', 'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 1, 1430),
('6', 'user1', 'STOCK_BUY', '2020-02-01 00:00:00', 'YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 1, 1468),
('7', 'user1', 'USER_LOGIN', '2020-01-01 00:00:00', NULL, NULL, NULL, NULL),
('8', 'user2', 'USER_LOGIN', '2020-02-01 00:00:00', NULL, NULL, NULL, NULL)
ON CONFLICT DO NOTHING;

