package app.viaverse.identity.consent.application;

import app.viaverse.identity.consent.domain.ConsentInput;
import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConsentPolicy {
    private static final Set<ConsentTypeEnum> REQUIRED_CONSENTS = Set.of(
            ConsentTypeEnum.TERMS_OF_SERVICE,
            ConsentTypeEnum.PERSONAL_DATA_PROTECTION_LAW
    );

    public void validateRequiredConsents(List<ConsentInput> requiredConsents) {
        if (requiredConsents == null || requiredConsents.isEmpty()) {
            throw IdentityErrors.requiredConsentsMissing(Map.of("requiredConsents", "are required"));
        }

        Set<ConsentTypeEnum> acceptedTypes = requiredConsents.stream()
                .filter(consent -> consent.version() != null && !consent.version().isBlank())
                .map(ConsentInput::type)
                .collect(Collectors.toSet());
        if (!acceptedTypes.containsAll(REQUIRED_CONSENTS)) {
            throw IdentityErrors.requiredConsentsMissing(Map.of(
                    "requiredConsents",
                    "must include TERMS_OF_SERVICE and PERSONAL_DATA_PROTECTION_LAW"
            ));
        }
    }
}
