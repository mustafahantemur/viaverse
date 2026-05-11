package app.viaverse.identity.infrastructure.security;

import java.util.UUID;

public record JwtPrincipal(UUID accountId, UUID sessionId) {
}
