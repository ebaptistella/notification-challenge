-- Seed users (en_US). Idempotent: insert only when email not present.
INSERT INTO users (name, email, phone)
SELECT 'Alice', 'alice@example.com', '+15550000001'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@example.com');

INSERT INTO users (name, email, phone)
SELECT 'Bob', 'bob@example.com', '+15550000002'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@example.com');

INSERT INTO users (name, email, phone)
SELECT 'Carol', 'carol@example.com', '+15550000003'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'carol@example.com');

-- user_category: link users to categories (at least one user with one category and one channel)
INSERT INTO user_category (user_id, category_id)
SELECT u.id, c.id FROM users u, category c
WHERE u.email = 'alice@example.com' AND c.code = 'sports'
ON CONFLICT (user_id, category_id) DO NOTHING;

INSERT INTO user_category (user_id, category_id)
SELECT u.id, c.id FROM users u, category c
WHERE u.email = 'alice@example.com' AND c.code = 'finance'
ON CONFLICT (user_id, category_id) DO NOTHING;

INSERT INTO user_category (user_id, category_id)
SELECT u.id, c.id FROM users u, category c
WHERE u.email = 'alice@example.com' AND c.code = 'movies'
ON CONFLICT (user_id, category_id) DO NOTHING;

INSERT INTO user_category (user_id, category_id)
SELECT u.id, c.id FROM users u, category c
WHERE u.email = 'bob@example.com' AND c.code = 'sports'
ON CONFLICT (user_id, category_id) DO NOTHING;

INSERT INTO user_category (user_id, category_id)
SELECT u.id, c.id FROM users u, category c
WHERE u.email = 'bob@example.com' AND c.code = 'finance'
ON CONFLICT (user_id, category_id) DO NOTHING;

INSERT INTO user_category (user_id, category_id)
SELECT u.id, c.id FROM users u, category c
WHERE u.email = 'carol@example.com' AND c.code = 'movies'
ON CONFLICT (user_id, category_id) DO NOTHING;

-- user_channel: link users to channels
INSERT INTO user_channel (user_id, channel_id)
SELECT u.id, c.id FROM users u, channel c
WHERE u.email = 'alice@example.com' AND c.code IN ('sms', 'email', 'push_notification')
ON CONFLICT (user_id, channel_id) DO NOTHING;

INSERT INTO user_channel (user_id, channel_id)
SELECT u.id, c.id FROM users u, channel c
WHERE u.email = 'bob@example.com' AND c.code IN ('sms', 'email')
ON CONFLICT (user_id, channel_id) DO NOTHING;

INSERT INTO user_channel (user_id, channel_id)
SELECT u.id, c.id FROM users u, channel c
WHERE u.email = 'carol@example.com' AND c.code = 'push_notification'
ON CONFLICT (user_id, channel_id) DO NOTHING;