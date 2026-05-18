CREATE TABLE business_profile (
    account_id UUID PRIMARY KEY,
    legal_name VARCHAR(180),
    trade_name VARCHAR(180),
    sector VARCHAR(32),
    tax_id VARCHAR(64),
    address_line VARCHAR(240),
    district VARCHAR(120),
    city VARCHAR(120),
    country VARCHAR(120),
    phone VARCHAR(64),
    email_public VARCHAR(320),
    logo_media_id UUID,
    opening_hours_json VARCHAR(2000),
    verification_status VARCHAR(32) NOT NULL,
    business_terms_version_accepted VARCHAR(64),
    rejection_reason VARCHAR(240),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_business_profile_sector CHECK (
        sector IS NULL OR sector IN ('PHARMACY', 'CLINIC', 'AGENCY', 'SHOP', 'SOFTWARE', 'OTHER')
    ),
    CONSTRAINT chk_business_profile_verification_status CHECK (
        verification_status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED')
    )
);
