package app.viaverse.identity.auth.domain.value;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;

public record NormalizedIdentifier(IdentifierTypeEnum type, String value) {
}
