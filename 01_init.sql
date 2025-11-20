CREATE TABLE IF NOT EXISTS subscribers
(
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_id BIGINT,
    telegram_user_id TEXT NOT NULL,
    price BIGINT,
    last_notification_time TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_subscribers_price
    ON subscribers (price)
    WHERE price IS NOT NULL;