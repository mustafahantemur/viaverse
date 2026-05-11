package app.viaverse.identity.application.auth;

import app.viaverse.identity.domain.auth.IdentifierType;

public record NormalizedIdentifier(IdentifierType type, String value) {
}
