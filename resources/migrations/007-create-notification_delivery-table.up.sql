CREATE TABLE IF NOT EXISTS notification_delivery (
  id              bigserial PRIMARY KEY,
  notification_id bigint NOT NULL REFERENCES notification (id) ON DELETE CASCADE,
  user_id         bigint NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  channel_id      bigint NOT NULL REFERENCES channel (id) ON DELETE RESTRICT,
  status          varchar(20) NOT NULL DEFAULT 'sent' CHECK (status IN ('sent', 'failed')),
  created_at      timestamptz NOT NULL DEFAULT now()
);