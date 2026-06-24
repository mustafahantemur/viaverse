package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.StartPasswordResetUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.web.logging.ObservedAction;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class StartPasswordResetUseCaseImpl implements StartPasswordResetUseCase {

    private final Clock clock;
    private final AuthProperties properties;
    private final IdentifierNormalizer identifierNormalizer;
    private final IdentifierRepository identifierRepository;
    private final AuthLoginFlowRepository flowRepository;
    private final OtpChallengeService otpChallengeService;
    private final AuthAbuseProtectionService abuseProtectionService;

    public StartPasswordResetUseCaseImpl(
            Clock clock,
            AuthProperties properties,
            IdentifierNormalizer identifierNormalizer,
            IdentifierRepository identifierRepository,
            AuthLoginFlowRepository flowRepository,
            OtpChallengeService otpChallengeService,
            AuthAbuseProtectionService abuseProtectionService
    ) {
        this.clock = clock;
        this.properties = properties;
        this.identifierNormalizer = identifierNormalizer;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
        this.otpChallengeService = otpChallengeService;
        this.abuseProtectionService = abuseProtectionService;
    }

    @Override
    @ObservedAction("auth.password_reset_start")
    public Result execute(Command command) {
        Instant now = clock.instant();
        NormalizedIdentifier normalized = identifierNormalizer.normalize(command.identifier());
        abuseProtectionService.enforceStart(normalized, command.clientIp(), command.clientFingerprint());

        Instant expiresAt = now.plus(properties.getOtp().getTtl());
        Optional<IdentityIdentifier> identifier =
                identifierRepository.findByTypeAndValue(normalized.type(), normalized.value());
        if (identifier.isEmpty()) {
            // Don't leak existence. Return a synthetic flow id with the same
            // shape as a real flow so the client can't distinguish.
            return new Result(UUID.randomUUID(), normalized.type(), expiresAt, false);
        }

        // About to dispatch an OTP — apply the cooldown only here, not in
        // enforceStart (see AuthAbuseProtectionService for rationale).
        abuseProtectionService.enforceOtpResendCooldown(normalized);

        AuthLoginFlow flow = flowRepository.save(AuthLoginFlow.issue(
                UUID.randomUUID(),
                LoginFlowPurposeEnum.PASSWORD_RESET,
                normalized.type(),
                normalized.value(),
                identifier.get().accountId(),
                expiresAt,
                now
        ));
        otpChallengeService.issue(flow.getId(), normalized, expiresAt, now);
        return new Result(flow.getId(), normalized.type(), expiresAt, true);
    }
}

