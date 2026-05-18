CREATE TABLE profile (
    account_id UUID PRIMARY KEY,
    display_name VARCHAR(120) NOT NULL,
    first_name VARCHAR(80),
    last_name VARCHAR(80),
    avatar_media_id UUID,
    headline VARCHAR(80),
    bio VARCHAR(600),
    locale VARCHAR(32) NOT NULL,
    timezone VARCHAR(64) NOT NULL,
    active_mode VARCHAR(32) NOT NULL,
    completeness_score INTEGER NOT NULL,
    public_visibility VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_profile_completeness_score CHECK (completeness_score BETWEEN 0 AND 100),
    CONSTRAINT chk_profile_active_mode CHECK (active_mode IN ('CUSTOMER', 'INDIVIDUAL_PROVIDER', 'BUSINESS')),
    CONSTRAINT chk_profile_public_visibility CHECK (public_visibility IN ('PUBLIC', 'LIMITED', 'PRIVATE'))
);

CREATE TABLE profile_preference (
    account_id UUID NOT NULL,
    preference_key VARCHAR(160) NOT NULL,
    value_json JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (account_id, preference_key)
);

CREATE INDEX idx_profile_preference_account_id ON profile_preference (account_id);

CREATE TABLE profile_block (
    blocker_account_id UUID NOT NULL,
    blocked_account_id UUID NOT NULL,
    reason VARCHAR(200),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (blocker_account_id, blocked_account_id),
    CONSTRAINT chk_profile_block_not_self CHECK (blocker_account_id <> blocked_account_id)
);

CREATE INDEX idx_profile_block_blocked_account_id ON profile_block (blocked_account_id);
