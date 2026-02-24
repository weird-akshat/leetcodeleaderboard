CREATE TABLE IF NOT EXISTS leetcode_user_id (
                                                user_id VARCHAR(255) PRIMARY KEY
    );

CREATE TABLE IF NOT EXISTS app_users (
                                         id BIGSERIAL PRIMARY KEY,
                                         username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    leetcode_id VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS current_user_profile_state (
                                                          id BIGSERIAL PRIMARY KEY,
                                                          leetcode_id VARCHAR(255) NOT NULL UNIQUE,
    easy INTEGER DEFAULT 0,
    medium INTEGER DEFAULT 0,
    hard INTEGER DEFAULT 0,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS daily_user_profile_state (
                                                        id BIGSERIAL PRIMARY KEY,
                                                        leetcode_id VARCHAR(255) NOT NULL UNIQUE,
    easy INTEGER DEFAULT 0,
    medium INTEGER DEFAULT 0,
    hard INTEGER DEFAULT 0,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS weekly_user_profile_state (
                                                         id BIGSERIAL PRIMARY KEY,
                                                         leetcode_id VARCHAR(255) NOT NULL UNIQUE,
    easy INTEGER DEFAULT 0,
    medium INTEGER DEFAULT 0,
    hard INTEGER DEFAULT 0,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS monthly_user_profile_state (
                                                          id BIGSERIAL PRIMARY KEY,
                                                          leetcode_id VARCHAR(255) NOT NULL UNIQUE,
    easy INTEGER DEFAULT 0,
    medium INTEGER DEFAULT 0,
    hard INTEGER DEFAULT 0,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
    );