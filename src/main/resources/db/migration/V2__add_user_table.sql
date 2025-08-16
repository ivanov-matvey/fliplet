CREATE DOMAIN email AS citext
    CHECK (
        VALUE ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$'
        );

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username citext NOT NULL CONSTRAINT username_length CHECK (char_length(username) <= 32),
    name VARCHAR(100) NULL,
    email email NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    avatar_url TEXT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
