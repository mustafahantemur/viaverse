package app.viaverse.identity.shared.audit;

import app.viaverse.observability.audit.AuditAction;
import app.viaverse.observability.audit.AuditActor;
import app.viaverse.observability.audit.AuditContext;
import app.viaverse.observability.audit.AuditEvent;
import app.viaverse.observability.audit.AuditLogger;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class IdentityAuditEvents {
    private IdentityAuditEvents() {
    }

    public static void recordAccountSecurityEvent(
            AuditLogger auditLogger,
            UUID accountId,
            IdentityAuditEvent event
    ) {
        auditLogger.record(new AuditEvent(
                UUID.randomUUID(),
                Instant.now(),
                new AuditActor("ACCOUNT", accountId.toString()),
                AuditAction.TECHNICAL_ACCESS,
                "identity",
                accountId.toString(),
                new AuditContext(null, null, event.source()),
                Map.of()
        ));
    }
}
