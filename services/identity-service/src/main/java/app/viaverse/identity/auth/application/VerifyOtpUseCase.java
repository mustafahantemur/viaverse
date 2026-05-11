package app.viaverse.identity.auth.application;

import app.viaverse.identity.account.infrastructure.persistence.entity.IdentityAccountJpaEntity;
import app.viaverse.identity.auth.api.dto.VerifyOtpResponse;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.application.service.RegistrationTokenService;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthLoginFlowJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthOtpChallengeJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthLoginFlowJpaRepository;
import app.viaverse.identity.shared.audit.IdentityAuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEvents;
import app.viaverse.identity.shared.error.IdentityErrors;
import app.viaverse.identity.shared.error.IdentityException;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import app.viaverse.observability.audit.AuditLogger;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyOtpUseCase {
    private final AuthLoginFlowJpaRepository flowRepository;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final OtpChallengeService otpChallengeService;
    private final RegistrationTokenService registrationTokenService;
    private final AuthSessionIssuer sessionIssuer;
    private final AuditLogger auditLogger;

    public VerifyOtpUseCase(
            AuthLoginFlowJpaRepository flowRepository,
            AuthAbuseProtectionService abuseProtectionService,
            OtpChallengeService otpChallengeService,
            RegistrationTokenService registrationTokenService,
            AuthSessionIssuer sessionIssuer,
            AuditLogger auditLogger
    ) {
        this.flowRepository = flowRepository;
        this.abuseProtectionService = abuseProtectionService;
        this.otpChallengeService = otpChallengeService;
        this.registrationTokenService = registrationTokenService;
        this.sessionIssuer = sessionIssuer;
        this.auditLogger = auditLogger;
    }

    @Transactional(noRollbackFor = {IdentityException.class, RateLimitExceededException.class})
    public VerifyOtpResponse verify(UUID flowId, String otp, String userAgent, String clientIp) {
        Instant now = Instant.now();
        AuthLoginFlowJpaEntity flow = flowRepository.findById(flowId)
                .orElseThrow(() -> IdentityErrors.invalidAuthFlow(Map.of("flowId", "is invalid")));
        AuthOtpChallengeJpaEntity challenge = otpChallengeService.latestChallenge(flowId);

        abuseProtectionService.enforceOtpAttempt(flow, clientIp);
        otpChallengeService.validateWaitingForOtp(flow, challenge, now);
        if (!otpChallengeService.matches(challenge, otp)) {
            if (otpChallengeService.recordFailure(challenge)) {
                flow.fail(LoginFlowStatus.FAILED, now);
                abuseProtectionService.softLockOtp(flow);
            }
            throw IdentityErrors.invalidOtp();
        }

        otpChallengeService.verify(challenge, now);
        flow.markOtpVerified(now);
        if (flow.getAccountId() == null) {
            return registrationTokenService.requireRegistration(flow, now);
        }

        IdentityAccountJpaEntity account = sessionIssuer.activeAccount(flow.getAccountId());
        IdentityAuditEvents.recordAccountSecurityEvent(auditLogger, account.getId(), IdentityAuditEvent.LOGIN);
        return sessionIssuer.issue(account, userAgent, now);
    }
}
