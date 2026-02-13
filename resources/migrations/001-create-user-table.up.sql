CREATE TABLE IF NOT EXISTS users (
  id         bigserial PRIMARY KEY,
  name       varchar(255) NOT NULL,
  email      varchar(255) NOT NULL,
  phone      varchar(50),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);