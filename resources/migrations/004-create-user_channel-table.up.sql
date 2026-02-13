CREATE TABLE IF NOT EXISTS user_channel (
  user_id    bigint NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  channel_id bigint NOT NULL REFERENCES channel (id) ON DELETE CASCADE,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, channel_id)
);