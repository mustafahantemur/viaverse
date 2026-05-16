-- Add forensic context to audit_log: client IP and User-Agent.
-- These come from per-request MDC (populated by ClientContextFilter) and are
-- written by AuditLogAdapter alongside the existing correlation/request fields.

ALTER TABLE audit_log
    ADD COLUMN source_ip  VARCHAR(45),
    ADD COLUMN user_agent VARCHAR(255);

CREATE INDEX idx_audit_log_source_ip ON audit_log (source_ip);
