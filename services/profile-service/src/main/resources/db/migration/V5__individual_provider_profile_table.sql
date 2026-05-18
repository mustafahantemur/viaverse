CREATE TABLE individual_provider_profile (
    account_id UUID PRIMARY KEY,
    service_blurb VARCHAR(200),
    availability_summary VARCHAR(160),
    accepts_remote BOOLEAN NOT NULL DEFAULT FALSE,
    provider_terms_version_accepted VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);
