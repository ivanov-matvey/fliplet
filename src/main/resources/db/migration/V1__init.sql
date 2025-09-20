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

CREATE TABLE cards (
    id UUID PRIMARY KEY NOT NULL,
    card_collection_id UUID NOT NULL REFERENCES card_collections(id) ON DELETE CASCADE,
    front TEXT NOT NULL,
    back TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_cards_collection ON cards(card_collection_id);

CREATE TABLE card_progress (
    id UUID PRIMARY KEY NOT NULL,
    card_id UUID NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    repetition INT NOT NULL DEFAULT 0 CHECK (repetition >= 0),
    interval_days INT NOT NULL DEFAULT 1 CHECK (interval_days >= 0),
    ease_factor DOUBLE PRECISION NOT NULL DEFAULT 2.5 CHECK (ease_factor >= 1.3 AND ease_factor <= 3.5),
    last_review_at TIMESTAMPTZ,
    next_review_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_progress_card_user UNIQUE (card_id, user_id)
);

CREATE INDEX idx_progress_user_due ON card_progress (user_id, next_review_at);

CREATE TABLE reviews (
    id UUID PRIMARY KEY NOT NULL,
    card_id UUID NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reviewed_at TIMESTAMPTZ NOT NULL,
    quality SMALLINT NOT NULL CHECK (quality >= 0 AND quality <= 5),
    prev_interval_days INT NOT NULL,
    new_interval_days INT NOT NULL,
    prev_ease_factor DOUBLE PRECISION NOT NULL,
    new_ease_factor DOUBLE PRECISION NOT NULL,
    prev_repetitions INT NOT NULL,
    new_repetitions INT NOT NULL
);

CREATE INDEX idx_reviews_card_time ON reviews (card_id, reviewed_at);
CREATE INDEX idx_reviews_user_time ON reviews (user_id, reviewed_at);
