package app.viaverse.observability.audit;

public record AuditContext(String correlationId, String requestId, String source) {
}

