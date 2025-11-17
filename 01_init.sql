CREATE TABLE IF NOT EXISTS subscribers
(
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id TEXT NOT NULL,
    price BIGINT
);