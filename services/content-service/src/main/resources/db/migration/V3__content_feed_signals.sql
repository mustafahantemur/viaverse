CREATE TABLE content_interaction (
    id UUID PRIMARY KEY,
    viewer_account_id UUID NOT NULL,
    post_id UUID NOT NULL REFERENCES content_post (id) ON DELETE CASCADE,
    signal_type VARCHAR(32) NOT NULL,
    surface VARCHAR(80) NOT NULL,
    position INTEGER,
    dwell_time_ms BIGINT,
    session_id UUID,
    occurred_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_content_interaction_signal_type CHECK (
        signal_type IN (
            'IMPRESSION',
            'OPEN',
            'DWELL',
            'LIKE',
            'SAVE',
            'SHARE',
            'HIDE',
            'REPORT',
            'VIDEO_START',
            'VIDEO_25',
            'VIDEO_50',
            'VIDEO_75',
            'VIDEO_COMPLETE'
        )
    ),
    CONSTRAINT chk_content_interaction_position CHECK (position IS NULL OR position >= 0),
    CONSTRAINT chk_content_interaction_dwell CHECK (dwell_time_ms IS NULL OR dwell_time_ms >= 0)
);

CREATE INDEX idx_content_interaction_viewer_occurred_at
    ON content_interaction (viewer_account_id, occurred_at DESC);
CREATE INDEX idx_content_interaction_post_occurred_at
    ON content_interaction (post_id, occurred_at DESC);
CREATE INDEX idx_content_interaction_viewer_signal
    ON content_interaction (viewer_account_id, signal_type, occurred_at DESC);
