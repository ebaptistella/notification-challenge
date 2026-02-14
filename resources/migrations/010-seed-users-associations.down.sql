DELETE FROM user_channel WHERE user_id IN (SELECT id FROM users WHERE email IN ('alice@example.com', 'bob@example.com', 'carol@example.com'));
DELETE FROM user_category WHERE user_id IN (SELECT id FROM users WHERE email IN ('alice@example.com', 'bob@example.com', 'carol@example.com'));
DELETE FROM users WHERE email IN ('alice@example.com', 'bob@example.com', 'carol@example.com');