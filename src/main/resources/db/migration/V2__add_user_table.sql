CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    username_ci VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(10) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    avatar_url TEXT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
