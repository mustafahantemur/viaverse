package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.StartAuthUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.logging.ObservedAction;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Identifier triage:
 *
 * <ul>
 *   <li><b>Known identifier</b> → {@link AuthNextStepEnum#PASSWORD_REQUIRED}.
 *       No OTP is sent — login is now password-first.</li>
 *   <li><b>Unknown identifier</b> → start a {@link LoginFlowPurposeEnum#REGISTRATION}
 *       flow, dispatch an OTP to prove ownership, return
 *       {@link AuthNextStepEnum#OTP_REQUIRED}. The OTP is consumed by
 *       {@code POST /auth/register/verify-otp}, which then hands back a
 *       registration token for {@code POST /auth/register/complete}.</li>
 * </ul>
 */
@Service
public class StartAuthUseCaseImpl implements StartAuthUseCase {

    private final Clock clock;
    private final AuthProperties properties;
    private final IdentifierNormalizer identifierNormalizer;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final OtpChallengeService otpChallengeService;
    private final IdentifierRepository identifierRepository;
    private final AuthLoginFlowRepository flowRepository;

    public StartAuthUseCaseImpl(
            Clock clock,
            AuthProperties properties,
            IdentifierNormalizer identifierNormalizer,
            AuthAbuseProtectionService abuseProtectionService,
            OtpChallengeService otpChallengeService,
            IdentifierRepository identifierRepository,
            AuthLoginFlowRepository flowRepository
    ) {
        this.clock = clock;
        this.properties = properties;
        this.identifierNormalizer = identifierNormalizer;
        this.abuseProtectionService = abuseProtectionService;
        this.otpChallengeService = otpChallengeService;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
    }

    @Override
    @ObservedAction("auth.start")
    public Result execute(Command command) {
        Instant now = clock.instant();
        NormalizedIdentifier normalized = identifierNormalizer.normalize(command.identifier());
        abuseProtectionService.enforceStart(normalized, command.clientIp(), command.clientFingerprint());

        boolean known = identifierRepository
                .findByTypeAndValue(normalized.type(), normalized.value())
                .isPresent();
        if (known) {
            return new Result(
                    null,
                    normalized.type(),
                    AuthNextStepEnum.PASSWORD_REQUIRED,
                    null
            );
        }

        Instant expiresAt = now.plus(properties.getOtp().getTtl());
        AuthLoginFlow flow = flowRepository.save(AuthLoginFlow.issue(
                UUID.randomUUID(),
                LoginFlowPurposeEnum.REGISTRATION,
                normalized.type(),
                normalized.value(),
                null,
                expiresAt,
                now
        ));
        otpChallengeService.issue(flow.getId(), normalized, expiresAt, now);
        return new Result(
                flow.getId(),
                normalized.type(),
                AuthNextStepEnum.OTP_REQUIRED,
                expiresAt
        );
    }
}
