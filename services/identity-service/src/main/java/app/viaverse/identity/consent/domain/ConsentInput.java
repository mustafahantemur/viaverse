package app.viaverse.identity.consent.domain;

import app.viaverse.identity.consent.domain.ConsentType;

public record ConsentInput(ConsentType type, String version) {
}
