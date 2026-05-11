package app.viaverse.identity.auth.domain.policy;

import app.viaverse.identity.shared.error.IdentityErrors;
import org.springframework.stereotype.Component;

@Component
public class RegistrationPolicy {
    public void validateProfile(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw IdentityErrors.displayNameRequired();
        }
    }
}
