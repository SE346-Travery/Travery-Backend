CREATE TABLE user_device_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    fcm_token TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_device_tokens_email FOREIGN KEY (email) REFERENCES users(email) ON DELETE CASCADE,
    CONSTRAINT uk_user_device_tokens_token UNIQUE (fcm_token)
);

CREATE INDEX idx_user_device_tokens_email ON user_device_tokens(email);
CREATE INDEX idx_user_device_tokens_fcm_token ON user_device_tokens(fcm_token);
