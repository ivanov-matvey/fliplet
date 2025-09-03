CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username CITEXT NOT NULL UNIQUE CONSTRAINT username_length CHECK (char_length(username) <= 32),
    name VARCHAR(100),
    email CITEXT NOT NULL UNIQUE,
    pending_email CITEXT,
    password_hash TEXT NOT NULL,
    avatar_url TEXT NOT NULL,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    pending_email_requested_at TIMESTAMPTZ
);

CREATE TABLE card_collections (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_collections_user ON card_collections(user_id);
