CREATE TABLE content_post (
    id UUID PRIMARY KEY,
    author_account_id UUID NOT NULL,
    author_mode VARCHAR(32) NOT NULL,
    post_type VARCHAR(32) NOT NULL,
    title VARCHAR(160),
    body VARCHAR(4000) NOT NULL,
    city VARCHAR(120),
    district VARCHAR(120),
    event_starts_at TIMESTAMPTZ,
    event_ends_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL,
    moderation_status VARCHAR(32) NOT NULL,
    published_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_content_post_author_mode CHECK (
        author_mode IN ('CUSTOMER', 'INDIVIDUAL_PROVIDER', 'BUSINESS')
    ),
    CONSTRAINT chk_content_post_type CHECK (
        post_type IN ('LOCAL_UPDATE', 'ANNOUNCEMENT', 'EVENT', 'ADVICE', 'BUSINESS_PROMOTION')
    ),
    CONSTRAINT chk_content_post_status CHECK (
        status IN ('PUBLISHED', 'WITHDRAWN', 'REJECTED')
    ),
    CONSTRAINT chk_content_post_moderation_status CHECK (
        moderation_status IN ('AUTO_APPROVED', 'PENDING_REVIEW', 'REJECTED')
    ),
    CONSTRAINT chk_content_post_event_window CHECK (
        event_ends_at IS NULL OR event_starts_at IS NULL OR event_starts_at <= event_ends_at
    )
);

CREATE INDEX idx_content_post_author_created_at ON content_post (author_account_id, created_at DESC);
CREATE INDEX idx_content_post_status_published_at ON content_post (status, published_at DESC);
CREATE INDEX idx_content_post_locality_published_at ON content_post (city, district, published_at DESC);

CREATE TABLE content_post_media (
    post_id UUID NOT NULL REFERENCES content_post (id) ON DELETE CASCADE,
    sort_order INTEGER NOT NULL,
    media_asset_id UUID NOT NULL,
    PRIMARY KEY (post_id, sort_order),
    CONSTRAINT uq_content_post_media_asset UNIQUE (post_id, media_asset_id)
);
