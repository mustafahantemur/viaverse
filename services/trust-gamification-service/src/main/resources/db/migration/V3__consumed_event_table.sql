CREATE TABLE consumed_event (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(160) NOT NULL,
    consumed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_consumed_event_event_type ON consumed_event (event_type);
