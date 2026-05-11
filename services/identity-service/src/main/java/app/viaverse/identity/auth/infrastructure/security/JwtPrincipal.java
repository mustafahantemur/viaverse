package app.viaverse.identity.auth.infrastructure.security;

import java.util.UUID;

public record JwtPrincipal(UUID accountId, UUID sessionId) {
}
