package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.VerifyOtpUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.application.service.RegistrationTokenService;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowPurposeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatusEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.OtpChallenge;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class VerifyOtpUseCaseImpl implements VerifyOtpUseCase {

    private final Clock clock;
    private final AuthLoginFlowRepository flowRepository;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final OtpChallengeService otpChallengeService;
    private final RegistrationTokenService registrationTokenService;

    public VerifyOtpUseCaseImpl(
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
    @ObservedAction("otp.verify")
    @AuditEvent(IdentityAuditEventEnum.OTP_VERIFIED)
    public Result execute(Command command) {
        Instant now = clock.instant();
        AuthLoginFlow flow = flowRepository.findById(command.flowId())
                .orElseThrow(() -> IdentityErrors.invalidAuthFlow(Map.of("flowId", "is invalid")));
        if (flow.getPurpose() != LoginFlowPurposeEnum.REGISTRATION) {
            throw IdentityErrors.invalidAuthFlow(Map.of("flowId", "is not a registration flow"));
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
        RegistrationTokenService.Issued registration = registrationTokenService.requireRegistration(flow, now);
        return new Result(
                AuthNextStepEnum.REGISTRATION_REQUIRED,
                registration.registrationToken(),
                registration.expiresAt()
        );
    }
}
