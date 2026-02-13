CREATE TABLE IF NOT EXISTS user_category (
  user_id     bigint NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  category_id bigint NOT NULL REFERENCES category (id) ON DELETE CASCADE,
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, category_id)
);