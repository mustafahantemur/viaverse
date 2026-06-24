CREATE TABLE service_request (
    id UUID PRIMARY KEY,
    requester_account_id UUID NOT NULL,
    title VARCHAR(160) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    category VARCHAR(64) NOT NULL,
    budget_min_amount_minor BIGINT,
    budget_max_amount_minor BIGINT,
    currency VARCHAR(3) NOT NULL,
    remote_allowed BOOLEAN NOT NULL DEFAULT FALSE,
    district VARCHAR(120),
    city VARCHAR(120),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_service_request_category CHECK (
        category IN (
            'HOME_REPAIR',
            'DIGITAL_SOFTWARE',
            'CREATIVE_MEDIA',
            'EDUCATION',
            'CLEANING',
            'LOGISTICS',
            'CARE_HEALTH',
            'PROFESSIONAL_CONSULTING',
            'PETS',
            'EVENTS',
            'LOCAL_HELP'
        )
    ),
    CONSTRAINT chk_service_request_status CHECK (status IN ('OPEN', 'MATCHED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_service_request_budget_min CHECK (
        budget_min_amount_minor IS NULL OR budget_min_amount_minor >= 0
    ),
    CONSTRAINT chk_service_request_budget_max CHECK (
        budget_max_amount_minor IS NULL OR budget_max_amount_minor >= 0
    ),
    CONSTRAINT chk_service_request_budget_range CHECK (
        budget_min_amount_minor IS NULL OR budget_max_amount_minor IS NULL
        OR budget_min_amount_minor <= budget_max_amount_minor
    )
);

CREATE INDEX idx_service_request_status_created_at ON service_request (status, created_at DESC);
CREATE INDEX idx_service_request_requester_created_at ON service_request (requester_account_id, created_at DESC);

CREATE TABLE service_request_media (
    request_id UUID NOT NULL REFERENCES service_request (id) ON DELETE CASCADE,
    sort_order INTEGER NOT NULL,
    media_asset_id UUID NOT NULL,
    PRIMARY KEY (request_id, sort_order),
    CONSTRAINT uq_service_request_media_asset UNIQUE (request_id, media_asset_id)
);

CREATE TABLE offer (
    id UUID PRIMARY KEY,
    request_id UUID NOT NULL REFERENCES service_request (id),
    provider_account_id UUID NOT NULL,
    amount_minor BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    message VARCHAR(1000),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_offer_amount CHECK (amount_minor >= 0),
    CONSTRAINT chk_offer_status CHECK (status IN ('SUBMITTED', 'ACCEPTED', 'REJECTED', 'WITHDRAWN')),
    CONSTRAINT uq_offer_request_provider UNIQUE (request_id, provider_account_id)
);

CREATE INDEX idx_offer_request_created_at ON offer (request_id, created_at DESC);
CREATE INDEX idx_offer_provider_created_at ON offer (provider_account_id, created_at DESC);

CREATE TABLE job (
    id UUID PRIMARY KEY,
    request_id UUID NOT NULL UNIQUE REFERENCES service_request (id),
    accepted_offer_id UUID NOT NULL UNIQUE REFERENCES offer (id),
    requester_account_id UUID NOT NULL,
    provider_account_id UUID NOT NULL,
    agreed_amount_minor BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_job_amount CHECK (agreed_amount_minor >= 0),
    CONSTRAINT chk_job_status CHECK (status IN ('AGREED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'DISPUTED'))
);

CREATE INDEX idx_job_requester_created_at ON job (requester_account_id, created_at DESC);
CREATE INDEX idx_job_provider_created_at ON job (provider_account_id, created_at DESC);
