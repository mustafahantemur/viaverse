package app.viaverse.identity.auth.domain.value;

import app.viaverse.identity.auth.domain.enums.IdentifierType;

public record NormalizedIdentifier(IdentifierType type, String value) {
}
