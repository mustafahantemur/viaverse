CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    occurred_at TIMESTAMPTZ NOT NULL,
    actor_type VARCHAR(64) NOT NULL,
    actor_id VARCHAR(160) NOT NULL,
    action VARCHAR(80) NOT NULL,
    resource_type VARCHAR(160),
    resource_id VARCHAR(160),
    correlation_id VARCHAR(160),
    request_id VARCHAR(160),
    source VARCHAR(160),
    metadata_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_log_occurred_at ON audit_log (occurred_at);
CREATE INDEX idx_audit_log_correlation_id ON audit_log (correlation_id);
CREATE INDEX idx_audit_log_action ON audit_log (action);

CREATE TABLE outbox_event (
    id UUID PRIMARY KEY,
    event_type VARCHAR(160) NOT NULL,
    payload_json JSONB NOT NULL,
    headers_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    status VARCHAR(32) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    available_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_outbox_event_status_available_at ON outbox_event (status, available_at);
CREATE INDEX idx_outbox_event_event_type ON outbox_event (event_type);
