package app.viaverse.observability.audit;

public interface AuditLogger {
    void record(AuditEvent event);
}

