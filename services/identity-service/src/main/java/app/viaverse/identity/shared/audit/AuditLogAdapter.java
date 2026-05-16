package app.viaverse.identity.shared.audit;

import app.viaverse.observability.audit.AuditContext;
import app.viaverse.observability.audit.AuditEvent;
import app.viaverse.observability.audit.AuditLogger;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuditLogAdapter implements AuditLogger {
    private final AuditLogJpaRepository repository;

    public AuditLogAdapter(AuditLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void record(AuditEvent event) {
        AuditContext context = event.context();
        repository.save(new AuditLogJpaEntity(
                event.id(),
                event.occurredAt(),
                event.actor().type(),
                event.actor().id(),
                event.action(),
                event.resourceType(),
                event.resourceId(),
                context == null ? null : context.correlationId(),
                context == null ? null : context.requestId(),
                context == null ? null : context.source(),
                context == null ? null : context.sourceIp(),
                context == null ? null : context.userAgent(),
                metadataJson(event.metadata()),
                Instant.now()
        ));
    }
    private String metadataJson(Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "{}";
        }
        return metadata.entrySet().stream()
                .map(entry -> quote(entry.getKey()) + ":" + quote(entry.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }

    private String quote(String value) {
        String safeValue = value == null ? "" : value;
        return "\"" + safeValue
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }
}

