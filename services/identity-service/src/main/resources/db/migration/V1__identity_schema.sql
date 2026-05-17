-- ============================================================
-- Identity Service – unified schema (fresh install)
-- ============================================================

-- ---- Technical infrastructure ---------------------

CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    occurred_at TIMESTAMPTZ NOT NULL,
    actor_type VARCHAR(64) NOT NULL,
    actor_id VARCHAR(160) NOT NULL,
    action VARCHAR(80) NOT NULL,
    resource_type VARCHAR(160),
    resource_id VARCHAR(160),
    correlation_id VARCHAR(160),
    request_id VARCHAR(160),
    source VARCHAR(160),
    source_ip VARCHAR(45),
    user_agent VARCHAR(255),
    metadata_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_log_occurred_at ON audit_log (occurred_at);
CREATE INDEX idx_audit_log_correlation_id ON audit_log (correlation_id);
CREATE INDEX idx_audit_log_action ON audit_log (action);
CREATE INDEX idx_audit_log_source_ip ON audit_log (source_ip);

CREATE TABLE outbox_event (
    id UUID PRIMARY KEY,
    event_type VARCHAR(160) NOT NULL,
    payload_json JSONB NOT NULL,
    headers_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    status VARCHAR(32) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    available_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_outbox_event_status_available_at ON outbox_event (status, available_at);
CREATE INDEX idx_outbox_event_event_type ON outbox_event (event_type);

-- ---- Identity / Account --------------------------

CREATE TABLE identity_account (
    id UUID PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    first_name VARCHAR(80),
    last_name VARCHAR(80),
    profile_completed BOOLEAN NOT NULL DEFAULT FALSE,
    -- Argon2id-encoded password. NULL means social-only account; the user can
    -- always add a password later via /me/password/set. Stored opaquely; the
    -- algorithm and parameters are encoded in the hash string itself, so we
    -- can rotate parameters without a schema change.
    password_hash VARCHAR(255),
    password_updated_at TIMESTAMPTZ,
    -- TOTP-based 2FA state. two_factor_secret is the raw 20-byte HMAC-SHA1
    -- secret encrypted at rest (AES-GCM, key derived from KMS / config). The
    -- secret is only readable inside the identity-service.
    two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_secret BYTEA,
    two_factor_enrolled_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_identity_account_status ON identity_account (status);

CREATE TABLE identity_account_role (
    account_id UUID NOT NULL REFERENCES identity_account (id),
    role VARCHAR(32) NOT NULL,
    PRIMARY KEY (account_id, role)
);

-- Single-use recovery codes for 2FA. Hashed with the same TokenHasher as
-- refresh / registration tokens. used_at is stamped when consumed; rows are
-- never deleted so we can show "X of N backup codes used" in profile UI.
CREATE TABLE account_backup_code (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES identity_account (id),
    code_hash VARCHAR(128) NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_account_backup_code_hash UNIQUE (code_hash)
);

CREATE INDEX idx_account_backup_code_account_id ON account_backup_code (account_id);

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

-- ---- Auth login flow -----------------------------

-- A login flow is a short-lived state machine. `purpose` distinguishes what
-- the OTP is being used for so we never accidentally cross flows:
--   REGISTRATION         — new account, identifier ownership proof
--   IDENTIFIER_VERIFY    — existing account adding a new email/phone
--   PASSWORD_RESET       — recover access via OTP to known identifier
--   TWO_FACTOR_SETUP     — proof-of-ownership for /me/2fa/* operations
-- Password-based login does NOT create a login flow row; it goes through
-- AuthSession directly. TOTP for 2FA login uses a short-lived partial-auth
-- token issued from JWT, not this table.
CREATE TABLE auth_login_flow (
    id UUID PRIMARY KEY,
    purpose VARCHAR(32) NOT NULL,
    identifier_type VARCHAR(16) NOT NULL,
    normalized_identifier VARCHAR(320) NOT NULL,
    account_id UUID REFERENCES identity_account (id),
    status VARCHAR(32) NOT NULL,
    -- Sticky bit: true when the flow was bootstrapped by an external IdP
    -- (Google / Apple) rather than an OTP. /auth/register uses this to
    -- decide whether the user must provide a password (OTP path = yes,
    -- social path = no, since the IdP already proved identifier ownership).
    external_verified BOOLEAN NOT NULL DEFAULT FALSE,
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
CREATE INDEX idx_auth_login_flow_purpose ON auth_login_flow (purpose);

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

-- ---- Sessions & refresh tokens -------------------

CREATE TABLE auth_session (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES identity_account (id),
    status VARCHAR(32) NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    last_seen_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    user_agent VARCHAR(512),
    device_id VARCHAR(255),
    device_name VARCHAR(100),
    platform VARCHAR(20),
    last_ip VARCHAR(45),
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

-- ---- Consent -------------------------------------

CREATE TABLE consent_record (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES identity_account (id),
    consent_type VARCHAR(64) NOT NULL,
    consent_category VARCHAR(32) NOT NULL,
    version VARCHAR(64) NOT NULL,
    accepted BOOLEAN NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL,
    source VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_consent_record_account_type_version UNIQUE (account_id, consent_type, version)
);

CREATE INDEX idx_consent_record_account_id ON consent_record (account_id);
CREATE INDEX idx_consent_record_type_version ON consent_record (consent_type, version);

-- ---- Rate limiting -------------------------------

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

-- ---- Admin invitation ----------------------------

CREATE TABLE admin_invitation (
    id UUID PRIMARY KEY,
    token_hash VARCHAR(128) NOT NULL,
    issued_by_account_id UUID NOT NULL REFERENCES identity_account (id),
    expires_at TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_admin_invitation_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_admin_invitation_expires_at ON admin_invitation (expires_at);
