-- Create users table with audit fields
CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(10)  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_user_role ON users (role);
CREATE INDEX idx_user_created_at ON users (created_at);
CREATE INDEX idx_user_modified_at ON users (modified_at);
