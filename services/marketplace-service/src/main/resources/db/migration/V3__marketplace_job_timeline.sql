CREATE TABLE job_timeline_entry (
    id UUID PRIMARY KEY,
    job_id UUID NOT NULL REFERENCES job (id) ON DELETE CASCADE,
    actor_account_id UUID,
    event_type VARCHAR(40) NOT NULL,
    message VARCHAR(1000),
    occurred_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_job_timeline_event_type CHECK (
        event_type IN ('JOB_CREATED', 'JOB_STARTED', 'JOB_COMPLETED', 'NOTE_ADDED')
    ),
    CONSTRAINT chk_job_timeline_note_message CHECK (
        event_type <> 'NOTE_ADDED'
        OR (message IS NOT NULL AND length(trim(message)) > 0)
    )
);

CREATE INDEX idx_job_timeline_job_occurred_at
    ON job_timeline_entry (job_id, occurred_at ASC, created_at ASC);

