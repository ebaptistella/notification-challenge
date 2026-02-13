CREATE TABLE IF NOT EXISTS channel (
  id         bigserial PRIMARY KEY,
  code       varchar(50) NOT NULL UNIQUE,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);