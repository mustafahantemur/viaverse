CREATE TABLE individual_provider_service_category (
    account_id UUID NOT NULL,
    category VARCHAR(64) NOT NULL,
    PRIMARY KEY (account_id, category),
    CONSTRAINT fk_individual_provider_service_category_profile
        FOREIGN KEY (account_id) REFERENCES individual_provider_profile (account_id) ON DELETE CASCADE,
    CONSTRAINT chk_individual_provider_service_category_value CHECK (
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
    )
);

CREATE INDEX idx_individual_provider_service_category_category
    ON individual_provider_service_category (category);

CREATE TABLE business_service_category (
    account_id UUID NOT NULL,
    category VARCHAR(64) NOT NULL,
    PRIMARY KEY (account_id, category),
    CONSTRAINT fk_business_service_category_profile
        FOREIGN KEY (account_id) REFERENCES business_profile (account_id) ON DELETE CASCADE,
    CONSTRAINT chk_business_service_category_value CHECK (
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
    )
);

CREATE INDEX idx_business_service_category_category
    ON business_service_category (category);
