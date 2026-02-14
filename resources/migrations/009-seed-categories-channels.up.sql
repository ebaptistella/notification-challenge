-- Seed categories and channels (en_US). Idempotent: ON CONFLICT DO NOTHING.
INSERT INTO category (code) VALUES ('sports'), ('finance'), ('movies')
  ON CONFLICT (code) DO NOTHING;

INSERT INTO channel (code) VALUES ('sms'), ('email'), ('push_notification')
  ON CONFLICT (code) DO NOTHING;