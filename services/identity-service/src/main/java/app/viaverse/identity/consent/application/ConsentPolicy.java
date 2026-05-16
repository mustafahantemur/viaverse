package app.viaverse.identity.consent.application;

import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.consent.domain.ConsentCategoryEnum;
import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.consent.domain.RequiredConsentDocument;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.stereotype.Component;

/**
 * Server-owned consent document registry. Clients see only the {@link ConsentTypeEnum}
 * identifiers and acknowledge them — the current published version comes from
 * {@link AuthProperties.Consent} and is stamped onto the {@code consent_record}
 * at acceptance time. Bumping a version in config invalidates prior acceptances
 * for that document so the user is re-prompted on next sensitive flow.
 */
@Component
public class ConsentPolicy {
    private static final Set<ConsentTypeEnum> REQUIRED_TYPES = Set.of(
            ConsentTypeEnum.TERMS_OF_SERVICE,
            ConsentTypeEnum.PERSONAL_DATA_PROTECTION_LAW
    );

    private final AuthProperties properties;

    public ConsentPolicy(AuthProperties properties) {
        this.properties = properties;
    }

    public Set<ConsentTypeEnum> requiredTypes() {
        return REQUIRED_TYPES;
    }

    public List<RequiredConsentDocument> requiredDocuments() {
        return REQUIRED_TYPES.stream()
                .sorted()
                .map(type -> new RequiredConsentDocument(
                        type,
                        ConsentCategoryEnum.REQUIRED_LEGAL,
                        currentVersion(type),
                        currentUrl(type)
                ))
                .toList();
    }

    public RequiredConsentDocument marketingDocument() {
        return new RequiredConsentDocument(
                ConsentTypeEnum.MARKETING_COMMUNICATION,
                ConsentCategoryEnum.OPTIONAL_MARKETING,
                properties.getConsent().getMarketingVersion(),
                properties.getConsent().getMarketingUrl()
        );
    }

    public String currentVersion(ConsentTypeEnum type) {
        return switch (type) {
            case TERMS_OF_SERVICE -> properties.getConsent().getTermsOfServiceVersion();
            case PERSONAL_DATA_PROTECTION_LAW -> properties.getConsent().getPersonalDataProtectionLawVersion();
            case MARKETING_COMMUNICATION -> properties.getConsent().getMarketingVersion();
        };
    }

    public String currentUrl(ConsentTypeEnum type) {
        return switch (type) {
            case TERMS_OF_SERVICE -> properties.getConsent().getTermsOfServiceUrl();
            case PERSONAL_DATA_PROTECTION_LAW -> properties.getConsent().getPersonalDataProtectionLawUrl();
            case MARKETING_COMMUNICATION -> properties.getConsent().getMarketingUrl();
        };
    }

    /**
     * Asserts the supplied set of accepted types includes every required type.
     * Throws a validation error naming the missing types so the client can fix it.
     */
    public void validateRequiredConsents(Collection<ConsentTypeEnum> acceptedTypes) {
        Set<ConsentTypeEnum> accepted = acceptedTypes == null ? Set.of() : new HashSet<>(acceptedTypes);
        List<String> missing = new ArrayList<>();
        for (ConsentTypeEnum required : REQUIRED_TYPES) {
            if (!accepted.contains(required)) {
                missing.add(required.name());
            }
        }
        if (!missing.isEmpty()) {
            Map<String, String> fieldErrors = new LinkedHashMap<>();
            for (String type : missing) {
                fieldErrors.put("requiredConsents." + type, "must be accepted");
            }
            throw IdentityErrors.requiredConsentsMissing(new TreeMap<>(fieldErrors));
        }
    }
}
