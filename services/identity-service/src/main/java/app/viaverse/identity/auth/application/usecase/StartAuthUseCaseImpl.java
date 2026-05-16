package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.StartAuthUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.domain.enums.AuthNextStep;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.logging.ObservedAction;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

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
        abuseProtectionService.enforceStart(normalized, command.clientIp(), command.clientFingerprint(), now);

        UUID accountId = identifierRepository
                .findByTypeAndValue(normalized.type(), normalized.value())
                .map(identifier -> identifier.accountId())
                .orElse(null);

        Instant expiresAt = now.plus(properties.getOtp().getTtl());
        AuthLoginFlow flow = flowRepository.save(AuthLoginFlow.issue(
                UUID.randomUUID(),
                normalized.type(),
                normalized.value(),
                accountId,
                expiresAt,
                now
        ));
        String debugOtp = otpChallengeService.issue(flow.getId(), normalized, expiresAt, now);
        return new Result(
                flow.getId(),
                normalized.type(),
                AuthNextStep.OTP_REQUIRED,
                expiresAt,
                debugOtp
        );
    }
}
