package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.account.domain.AccountStatus;
import app.viaverse.identity.account.infrastructure.adapter.out.persistence.entity.IdentityAccountJpaEntity;
import app.viaverse.identity.account.infrastructure.adapter.out.persistence.repository.IdentityAccountJpaRepository;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.entity.IdentityIdentifierJpaEntity;
import app.viaverse.identity.auth.infrastructure.adapter.out.persistence.repository.IdentityIdentifierJpaRepository;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.consent.domain.ConsentCategory;
import app.viaverse.identity.consent.domain.ConsentType;
import app.viaverse.identity.consent.infrastructure.adapter.out.persistence.entity.ConsentRecordJpaEntity;
import app.viaverse.identity.consent.infrastructure.adapter.out.persistence.repository.ConsentRecordJpaRepository;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LocalTestUserSeeder implements ApplicationRunner {
    public static final String EMAIL_IDENTIFIER = "test.user@viaverse.local";
    public static final String PHONE_IDENTIFIER = "+905551110000";

    private static final Set<ConsentType> REQUIRED_CONSENTS = Set.of(
            ConsentType.TERMS_OF_SERVICE,
            ConsentType.PERSONAL_DATA_PROTECTION_LAW
    );

    private final AuthProperties properties;
    private final Environment environment;
    private final IdentifierNormalizer identifierNormalizer;
    private final IdentityAccountJpaRepository accountRepository;
    private final IdentityIdentifierJpaRepository identifierRepository;
    private final ConsentRecordJpaRepository consentRepository;

    public LocalTestUserSeeder(
            AuthProperties properties,
            Environment environment,
            IdentifierNormalizer identifierNormalizer,
            IdentityAccountJpaRepository accountRepository,
            IdentityIdentifierJpaRepository identifierRepository,
            ConsentRecordJpaRepository consentRepository
    ) {
        this.properties = properties;
        this.environment = environment;
        this.identifierNormalizer = identifierNormalizer;
        this.accountRepository = accountRepository;
        this.identifierRepository = identifierRepository;
        this.consentRepository = consentRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedIfEnabled();
    }

    @Transactional
    public void seedIfEnabled() {
        if (!properties.getDebug().isSeedTestUsers() || !hasLocalOrTestProfile()) {
            return;
        }

        seedUser(EMAIL_IDENTIFIER, "Test User", "Test", "User");
        seedUser(PHONE_IDENTIFIER, "Test Phone User", "Test", "Phone");
    }

    private void seedUser(String rawIdentifier, String displayName, String firstName, String lastName) {
        Instant now = Instant.now();
        NormalizedIdentifier identifier = identifierNormalizer.normalize(rawIdentifier);
        UUID accountId = identifierRepository.findByIdentifierTypeAndNormalizedIdentifier(
                        identifier.type(),
                        identifier.value()
                )
                .map(IdentityIdentifierJpaEntity::getAccountId)
                .orElseGet(() -> createAccountWithIdentifier(identifier, displayName, firstName, lastName, now));
        ensureRequiredConsents(accountId, now);
    }

    private UUID createAccountWithIdentifier(
            NormalizedIdentifier identifier,
            String displayName,
            String firstName,
            String lastName,
            Instant now
    ) {
        UUID accountId = UUID.randomUUID();
        accountRepository.save(new IdentityAccountJpaEntity(
                accountId,
                AccountStatus.ACTIVE,
                displayName,
                firstName,
                lastName,
                true,
                now,
                now
        ));
        identifierRepository.save(new IdentityIdentifierJpaEntity(
                UUID.randomUUID(),
                accountId,
                identifier.type(),
                identifier.value(),
                now,
                now
        ));
        return accountId;
    }

    private void ensureRequiredConsents(UUID accountId, Instant now) {
        for (ConsentType consentType : REQUIRED_CONSENTS) {
            consentRepository.findByAccountIdAndConsentTypeAndVersion(accountId, consentType, "v1")
                    .orElseGet(() -> consentRepository.save(new ConsentRecordJpaEntity(
                            UUID.randomUUID(),
                            accountId,
                            consentType,
                            ConsentCategory.REQUIRED_LEGAL,
                            "v1",
                            true,
                            now,
                            "local-seed"
                    )));
        }
    }

    private boolean hasLocalOrTestProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equals("local") || profile.equals("test"));
    }
}
