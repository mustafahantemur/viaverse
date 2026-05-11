package app.viaverse.identity.shared.audit;

public enum IdentityAuditEvent {
    LOGIN("identity-login"),
    REGISTER("identity-register"),
    REFRESH("identity-refresh"),
    LOGOUT("identity-logout");

    private final String source;

    IdentityAuditEvent(String source) {
        this.source = source;
    }

    public String source() {
        return source;
    }
}
