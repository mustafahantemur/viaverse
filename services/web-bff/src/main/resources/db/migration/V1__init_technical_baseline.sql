-- web-bff currently holds no per-request state of its own; the file exists
-- so Flyway has a baseline to apply on a fresh DB and so the JPA validator
-- doesn't trip on schema absence. Future tables for BFF-side concerns
-- (e.g. anonymous device profiles, rate-limit buckets for unauthenticated
-- routes) land here.

CREATE TABLE bff_meta (
    key VARCHAR(64) PRIMARY KEY,
    value TEXT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
