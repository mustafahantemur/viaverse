package app.viaverse.identity.shared.error;

import java.util.UUID;

/**
 * Thrown by the refresh token rotation service when a refresh token that
 * has already been rotated or revoked is presented again. The
 * {@code RefreshTokenReuseAspect} converts this into an audit event plus
 * the canonical {@code INVALID_REFRESH_TOKEN} application exception.
 */
public class RefreshTokenReuseDetectedException extends RuntimeException {

    private final UUID sessionId;
    private final UUID accountId;

    public RefreshTokenReuseDetectedException(UUID sessionId, UUID accountId) {
        super("Refresh token reuse detected for session " + sessionId);
        this.sessionId = sessionId;
        this.accountId = accountId;
    }

    public UUID sessionId() {
        return sessionId;
    }

    public UUID accountId() {
        return accountId;
    }
}
