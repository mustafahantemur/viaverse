CREATE TABLE identity_account (
    id UUID PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    first_name VARCHAR(80),
    last_name VARCHAR(80),
    profile_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_identity_account_status ON identity_account (status);

CREATE TABLE identity_identifier (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES identity_account (id),
    identifier_type VARCHAR(16) NOT NULL,
    normalized_identifier VARCHAR(320) NOT NULL,
    verified_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_identity_identifier_type_value UNIQUE (identifier_type, normalized_identifier)
);

CREATE INDEX idx_identity_identifier_account_id ON identity_identifier (account_id);

CREATE TABLE auth_login_flow (
    id UUID PRIMARY KEY,
    identifier_type VARCHAR(16) NOT NULL,
    normalized_identifier VARCHAR(320) NOT NULL,
    account_id UUID REFERENCES identity_account (id),
    status VARCHAR(32) NOT NULL,
    registration_token_hash VARCHAR(128),
    registration_expires_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_auth_login_flow_identifier ON auth_login_flow (identifier_type, normalized_identifier);
CREATE INDEX idx_auth_login_flow_registration_token_hash ON auth_login_flow (registration_token_hash);
CREATE INDEX idx_auth_login_flow_status ON auth_login_flow (status);

CREATE TABLE auth_otp_challenge (
    id UUID PRIMARY KEY,
    flow_id UUID NOT NULL REFERENCES auth_login_flow (id),
    otp_hash VARCHAR(128) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    max_attempts INTEGER NOT NULL,
    status VARCHAR(32) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_auth_otp_challenge_flow_id ON auth_otp_challenge (flow_id);
CREATE INDEX idx_auth_otp_challenge_status ON auth_otp_challenge (status);

CREATE TABLE auth_session (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES identity_account (id),
    status VARCHAR(32) NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    last_seen_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    user_agent VARCHAR(512),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_auth_session_account_id ON auth_session (account_id);
CREATE INDEX idx_auth_session_status ON auth_session (status);

CREATE TABLE auth_refresh_token (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES auth_session (id),
    token_hash VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    replaced_by_token_id UUID REFERENCES auth_refresh_token (id),
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_auth_refresh_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_auth_refresh_token_session_id ON auth_refresh_token (session_id);
CREATE INDEX idx_auth_refresh_token_status ON auth_refresh_token (status);

CREATE TABLE consent_record (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES identity_account (id),
    consent_type VARCHAR(64) NOT NULL,
    consent_category VARCHAR(32) NOT NULL,
    version VARCHAR(64) NOT NULL,
    accepted BOOLEAN NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL,
    source VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_consent_record_account_id ON consent_record (account_id);
CREATE INDEX idx_consent_record_type_version ON consent_record (consent_type, version);
