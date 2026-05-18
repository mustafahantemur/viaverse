CREATE TABLE media_asset (
    id UUID PRIMARY KEY,
    owner_account_id UUID NOT NULL,
    asset_kind VARCHAR(32) NOT NULL,
    content_type VARCHAR(160) NOT NULL,
    original_file_name VARCHAR(255),
    object_key VARCHAR(255) NOT NULL,
    byte_size BIGINT,
    checksum_sha256 VARCHAR(128),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_media_asset_kind CHECK (asset_kind IN ('IMAGE', 'VIDEO')),
    CONSTRAINT chk_media_asset_status CHECK (status IN ('INITIATED', 'READY', 'FAILED')),
    CONSTRAINT chk_media_asset_byte_size CHECK (byte_size IS NULL OR byte_size >= 0)
);

CREATE INDEX idx_media_asset_owner_created_at ON media_asset (owner_account_id, created_at DESC);
CREATE INDEX idx_media_asset_status_created_at ON media_asset (status, created_at DESC);

CREATE TABLE media_upload_session (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL UNIQUE REFERENCES media_asset (id) ON DELETE CASCADE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_media_upload_session_expires_at ON media_upload_session (expires_at);
