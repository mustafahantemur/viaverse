package app.viaverse.identity.application.auth;

import app.viaverse.identity.domain.auth.ConsentType;

public record ConsentInput(ConsentType type, String version) {
}
