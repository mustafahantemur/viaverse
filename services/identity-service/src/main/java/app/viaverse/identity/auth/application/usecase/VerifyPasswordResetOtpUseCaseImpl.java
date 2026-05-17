package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.VerifyPasswordResetOtpUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.application.service.RegistrationTokenService;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatusEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.OtpChallenge;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyPasswordResetOtpUseCaseImpl implements VerifyPasswordResetOtpUseCase {

    private final Clock clock;
    private final AuthLoginFlowRepository flowRepository;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final OtpChallengeService otpChallengeService;
    private final RegistrationTokenService registrationTokenService;

    public VerifyPasswordResetOtpUseCaseImpl(
            Clock clock,
            AuthLoginFlowRepository flowRepository,
            AuthAbuseProtectionService abuseProtectionService,
            OtpChallengeService otpChallengeService,
            RegistrationTokenService registrationTokenService
    ) {
        this.clock = clock;
        this.flowRepository = flowRepository;
        this.abuseProtectionService = abuseProtectionService;
        this.otpChallengeService = otpChallengeService;
        this.registrationTokenService = registrationTokenService;
    }

    @Override
    @ObservedAction("auth.password_reset_verify_otp")
    @Transactional
    public Result execute(Command command) {
        Instant now = clock.instant();
        AuthLoginFlow flow = flowRepository.findById(command.flowId())
                .orElseThrow(() -> IdentityErrors.invalidAuthFlow(Map.of("flowId", "is invalid")));
        if (flow.getPurpose() != LoginFlowPurposeEnum.PASSWORD_RESET) {
            throw IdentityErrors.invalidAuthFlow(Map.of("flowId", "is not a password-reset flow"));
        }
        OtpChallenge challenge = otpChallengeService.latestChallenge(command.flowId());

        abuseProtectionService.enforceOtpAttempt(flow, command.clientIp());
        otpChallengeService.validateWaitingForOtp(flow, challenge, now);
        if (!otpChallengeService.matches(challenge, command.otp())) {
            if (otpChallengeService.recordFailure(challenge)) {
                flow.fail(LoginFlowStatusEnum.FAILED, now);
                abuseProtectionService.softLockOtp(flow);
            }
            throw IdentityErrors.invalidOtp();
        }

        otpChallengeService.verify(challenge, now);
        flow.markOtpVerified(now);
        // Re-use the registration-token machinery: same TTL semantics, same
        // hash storage, same one-shot consumption. The flow's purpose is what
        // distinguishes a reset token from a real registration token at
        // consume time.
        RegistrationTokenService.Issued issued = registrationTokenService.requireRegistration(flow, now);
        return new Result(issued.registrationToken(), issued.expiresAt());
    }
}
