CREATE TABLE IF NOT EXISTS notification (
  id         bigserial PRIMARY KEY,
  category_id bigint NOT NULL REFERENCES category (id) ON DELETE RESTRICT,
  body       text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);