package app.viaverse.observability.audit;

public record AuditActor(String type, String id) {
    public static AuditActor system() {
        return new AuditActor("SYSTEM", "system");
    }

    public AuditActor {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("actor type must not be blank");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("actor id must not be blank");
        }
    }
}

