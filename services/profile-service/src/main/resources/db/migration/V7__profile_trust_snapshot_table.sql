CREATE TABLE profile_trust_snapshot (
    account_id UUID PRIMARY KEY,
    score INTEGER NOT NULL,
    trust_level VARCHAR(32) NOT NULL,
    badge VARCHAR(32) NOT NULL,
    source_occurred_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_profile_trust_snapshot_score CHECK (score BETWEEN 0 AND 1000),
    CONSTRAINT chk_profile_trust_snapshot_level CHECK (
        trust_level IN ('BASIC', 'VERIFIED_HUMAN', 'ENHANCED')
    ),
    CONSTRAINT chk_profile_trust_snapshot_badge CHECK (
        badge IN ('BASIC', 'VERIFIED_HUMAN', 'ENHANCED')
    )
);
