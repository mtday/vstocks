
INSERT INTO stocks (market, symbol, name) VALUES
('TWITTER', 'realDonaldTrump',          'Donald J. Trump'),
('TWITTER', 'POTUS',                    'President Trump'),
('TWITTER', 'WhiteHouse',               'The White House'),
('TWITTER', 'JoeBiden',                 'Joe Biden'),
('TWITTER', 'BarackObama',              'Barack Obama'),
('YOUTUBE', 'UCLwNTXWEjVd2qIHLcXxQWxA', 'Timcast IRL'),
('YOUTUBE', 'UCe02lGcO-ahAURWuxAJnjdA', 'Tim Pool'),
('YOUTUBE', 'UCdN4aXTrHAtfgbVG9HjBmxQ', 'Key & Peele'),
('YOUTUBE', 'UCm9K6rby98W8JigLoZOh6FQ', 'LockPickingLawyer'),
('YOUTUBE', 'UCVTQuK2CaWaTgSsoNkn5AiQ', 'HBO')
ON CONFLICT ON CONSTRAINT stocks_pk DO NOTHING;

