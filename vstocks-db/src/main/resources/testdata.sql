
INSERT INTO users (id, email, username, display_name) VALUES
('user1', 'user1@domain.com', 'user1', 'User 1'),
('user2', 'user2@domain.com', 'user2', 'User 2'),
('user3', 'user3@domain.com', 'user3', 'User 3'),
('user4', 'user4@domain.com', 'user4', 'User 4');


INSERT INTO user_credits (user_id, credits) VALUES
('user1', 10000),
('user2', 10000),
('user3', 10000),
('user4', 10000);


INSERT INTO stocks (market, symbol, name) VALUES
('TWITTER', 'realDonaldTrump', 'Donald J. Trump'),
('TWITTER', 'POTUS',           'President Trump'),
('TWITTER', 'WhiteHouse',      'The White House'),
('YOUTUBE', 'Timcast',         'Tim Pool'),
('YOUTUBE', 'Timcast IRL',     'Timcast IRL');


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
('YOUTUBE', 'Timcast', '2020-01-01 00:00:00', 1430),
('YOUTUBE', 'Timcast', '2020-02-01 00:00:00', 1468),
('YOUTUBE', 'Timcast', '2020-03-01 00:00:00', 1502),
('YOUTUBE', 'Timcast IRL', '2020-01-01 00:00:00', 1230),
('YOUTUBE', 'Timcast IRL', '2020-02-01 00:00:00', 1268),
('YOUTUBE', 'Timcast IRL', '2020-03-01 00:00:00', 1302);


INSERT INTO user_stocks (user_id, market, symbol, shares) VALUES
('user1', 'TWITTER', 'realDonaldTrump', 2),
('user1', 'TWITTER', 'POTUS', 1),
('user1', 'YOUTUBE', 'Timcast', 3),
('user2', 'TWITTER', 'WhiteHouse', 2),
('user3', 'YOUTUBE', 'Timcast', 5);


INSERT INTO activity_logs (id, user_id, type, timestamp, market, symbol, shares, price) VALUES
('1', 'user1', 'STOCK_BUY', '2020-01-01 00:00:00', 'TWITTER', 'realDonaldTrump', 5, 4867),
('2', 'user2', 'STOCK_BUY', '2020-01-01 00:00:00', 'TWITTER', 'realDonaldTrump', 1, 4867),
('3', 'user2', 'STOCK_BUY', '2020-01-01 00:00:00', 'TWITTER', 'POTUS', 1, 2678),
('4', 'user2', 'STOCK_SELL', '2020-02-01 00:00:00', 'TWITTER', 'POTUS', 1, 2693);


SELECT user_id, timestamp FROM (
  SELECT user_id, MIN(timestamp) AS timestamp, COUNT(*) AS count FROM activity_logs
  WHERE user_id = ANY(?)
    AND user_id NOT IN (SELECT user_id FROM user_achievements WHERE achievement_id = ?)
    AND type = ? AND market = ?
  GROUP BY user_id
) AS data WHERE count = 1;

  SELECT DISTINCT ON (user_id) id, timestamp
  FROM activity_logs
  WHERE user_id IN ('user1', 'user2', 'user3', 'user4')
    AND user_id NOT IN (SELECT user_id FROM user_achievements WHERE achievement_id = 'whatever')
    AND type = 'STOCK_BUY' AND market = 'TWITTER'
  ORDER BY user_id, timestamp DESC;

WITH data AS (
  SELECT user_id, COUNT(user_id) AS count
  FROM activity_logs
  WHERE user_id IN ('user1', 'user2', 'user3', 'user4')
    AND user_id NOT IN (SELECT user_id FROM user_achievements WHERE achievement_id = 'whatever')
    AND type = 'STOCK_BUY' AND market = 'TWITTER'
  GROUP BY user_id
)
SELECT DISTINCT ON (a.user_id) a.user_id, a.timestamp, a.shares, a.price FROM activity_logs a
JOIN data ON (data.user_id = a.user_id)
WHERE data.count = 1
ORDER BY a.user_id, timestamp DESC;
