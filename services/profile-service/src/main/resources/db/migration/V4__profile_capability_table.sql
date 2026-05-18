CREATE TABLE profile_capability (
    account_id UUID NOT NULL,
    capability VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    enabled_at TIMESTAMPTZ,
    disabled_at TIMESTAMPTZ,
    verification_level VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (account_id, capability),
    CONSTRAINT chk_profile_capability_value CHECK (
        capability IN ('CUSTOMER', 'INDIVIDUAL_PROVIDER', 'BUSINESS')
    ),
    CONSTRAINT chk_profile_capability_status CHECK (
        status IN ('ENABLED', 'PENDING_REVIEW', 'SUSPENDED', 'DISABLED')
    ),
    CONSTRAINT chk_profile_capability_verification_level CHECK (
        verification_level IN ('NONE', 'BASIC', 'ENHANCED')
    )
);

CREATE INDEX idx_profile_capability_account_id ON profile_capability (account_id);
