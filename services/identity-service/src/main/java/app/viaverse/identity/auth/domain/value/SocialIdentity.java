package app.viaverse.identity.auth.domain.value;

import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import java.util.Objects;

public record SocialIdentity(
        SocialAuthProviderEnum provider,
        String subject,
        String email,
        boolean emailVerified
) {
    public SocialIdentity {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(subject, "subject");
    }

    public String normalizedIdentifier() {
        return provider.name() + ":" + subject;
    }
}
