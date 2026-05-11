CREATE TABLE auth_rate_limit_bucket (
    id UUID PRIMARY KEY,
    scope VARCHAR(64) NOT NULL,
    bucket_key VARCHAR(128) NOT NULL,
    window_start TIMESTAMPTZ NOT NULL,
    attempt_count INTEGER NOT NULL,
    locked_until TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_auth_rate_limit_bucket_scope_key UNIQUE (scope, bucket_key)
);

CREATE INDEX idx_auth_rate_limit_bucket_scope_window
    ON auth_rate_limit_bucket (scope, window_start);

CREATE INDEX idx_auth_rate_limit_bucket_locked_until
    ON auth_rate_limit_bucket (locked_until);
