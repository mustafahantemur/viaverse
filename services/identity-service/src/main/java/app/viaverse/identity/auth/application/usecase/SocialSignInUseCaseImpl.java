package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.SocialSignInUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.port.out.SocialAuthPort;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.PartialAuthTokenService;
import app.viaverse.identity.auth.application.service.RegistrationTokenService;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.auth.domain.value.SocialIdentity;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SocialSignInUseCaseImpl implements SocialSignInUseCase {

    private final Clock clock;
    private final AuthProperties properties;
    private final List<SocialAuthPort> socialAuthPorts;
    private final IdentifierNormalizer identifierNormalizer;
    private final IdentifierRepository identifierRepository;
    private final AuthLoginFlowRepository flowRepository;
    private final RegistrationTokenService registrationTokenService;
    private final AuthSessionIssuer sessionIssuer;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final PartialAuthTokenService partialAuthTokenService;

    public SocialSignInUseCaseImpl(
            Clock clock,
            AuthProperties properties,
            List<SocialAuthPort> socialAuthPorts,
            IdentifierNormalizer identifierNormalizer,
            IdentifierRepository identifierRepository,
            AuthLoginFlowRepository flowRepository,
            RegistrationTokenService registrationTokenService,
            AuthSessionIssuer sessionIssuer,
            AuthAbuseProtectionService abuseProtectionService,
            PartialAuthTokenService partialAuthTokenService
    ) {
        this.clock = clock;
        this.properties = properties;
        this.socialAuthPorts = socialAuthPorts;
        this.identifierNormalizer = identifierNormalizer;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
        this.registrationTokenService = registrationTokenService;
        this.sessionIssuer = sessionIssuer;
        this.abuseProtectionService = abuseProtectionService;
        this.partialAuthTokenService = partialAuthTokenService;
    }

    @Override
    @ObservedAction("auth.social.sign_in")
    @AuditEvent(IdentityAuditEventEnum.SOCIAL_SIGN_IN_SUCCEEDED)
    public Result execute(Command command) {
        Instant now = clock.instant();
        assertProviderEnabled(command.provider());

        SocialIdentity identity = socialAuthPorts.stream()
                .filter(port -> port.supports(command.provider()))
                .findFirst()
                .orElseThrow(() -> IdentityErrors.providerDisabled(command.provider().name()))
                .verify(command.idToken(), command.nonce());

        NormalizedIdentifier socialIdentifier = new NormalizedIdentifier(
                IdentifierTypeEnum.SOCIAL,
                identity.normalizedIdentifier()
        );
        abuseProtectionService.enforceSocialStart(
                socialIdentifier,
                command.clientIp(),
                command.clientFingerprint()
        );

        return identifierRepository
                .findByTypeAndValue(IdentifierTypeEnum.SOCIAL, socialIdentifier.value())
                .map(existing -> authenticate(existing.accountId(), command, now))
                .orElseGet(() -> linkExistingEmailOrRequireRegistration(identity, socialIdentifier, command, now));
    }

    private Result linkExistingEmailOrRequireRegistration(
            SocialIdentity identity,
            NormalizedIdentifier socialIdentifier,
            Command command,
            Instant now
    ) {
        if (identity.emailVerified() && identity.email() != null && !identity.email().isBlank()) {
            NormalizedIdentifier email = identifierNormalizer.normalize(identity.email());
            var existingEmail = identifierRepository.findByTypeAndValue(email.type(), email.value());
            if (existingEmail.isPresent()) {
                UUID accountId = existingEmail.get().accountId();
                identifierRepository.save(IdentityIdentifier.verify(
                        UUID.randomUUID(),
                        accountId,
                        IdentifierTypeEnum.SOCIAL,
                        socialIdentifier.value(),
                        now
                ));
                return authenticate(accountId, command, now);
            }
        }

        Instant expiresAt = now.plus(properties.getOtp().getTtl());
        AuthLoginFlow flow = flowRepository.save(AuthLoginFlow.issueExternallyVerified(
                UUID.randomUUID(),
                IdentifierTypeEnum.SOCIAL,
                socialIdentifier.value(),
                null,
                expiresAt,
                now
        ));
        RegistrationTokenService.Issued registration = registrationTokenService.requireRegistration(flow, now);
        return new Result(
                AuthNextStepEnum.REGISTRATION_REQUIRED,
                registration.registrationToken(),
                registration.expiresAt(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Result authenticate(UUID accountId, Command command, Instant now) {
        Account account = sessionIssuer.activeAccount(accountId);
        if (account.isTwoFactorEnabled()) {
            PartialAuthTokenService.Issued partial = partialAuthTokenService.issue(account.getId(), now);
            return new Result(
                    AuthNextStepEnum.TOTP_REQUIRED,
                    null,
                    null,
                    account.getId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    partial.token(),
                    partial.expiresAt()
            );
        }
        AuthSessionIssuer.Issued issued = sessionIssuer.issue(
                account,
                command.userAgent(),
                command.clientIp(),
                now
        );
        return new Result(
                AuthNextStepEnum.AUTHENTICATED,
                null,
                null,
                account.getId(),
                issued.session().getId(),
                issued.accessToken(),
                issued.accessTokenExpiresAt(),
                issued.refreshToken(),
                issued.refreshTokenExpiresAt(),
                null,
                null
        );
    }

    private void assertProviderEnabled(SocialAuthProviderEnum provider) {
        AuthProperties.Social social = properties.getSocial();
        boolean enabled = switch (provider) {
            case GOOGLE -> social.getGoogle().isEnabled();
            case APPLE -> social.getApple().isEnabled();
        };
        if (!enabled) {
            throw IdentityErrors.providerDisabled(provider.name());
        }
    }
}
