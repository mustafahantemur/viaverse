package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
import app.viaverse.identity.auth.domain.enums.OtpChallengeStatus;
import app.viaverse.identity.auth.domain.enums.RateLimitScope;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthLoginFlowJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthOtpChallengeJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthLoginFlowJpaRepository;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthOtpChallengeJpaRepository;
import app.viaverse.identity.auth.infrastructure.ratelimit.AuthRateLimiter;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class AuthAbuseProtectionService {
    private final AuthProperties properties;
    private final AuthRateLimiter rateLimiter;
    private final AuthLoginFlowJpaRepository flowRepository;
    private final AuthOtpChallengeJpaRepository challengeRepository;

    public AuthAbuseProtectionService(
            AuthProperties properties,
            AuthRateLimiter rateLimiter,
            AuthLoginFlowJpaRepository flowRepository,
            AuthOtpChallengeJpaRepository challengeRepository
    ) {
        this.properties = properties;
        this.rateLimiter = rateLimiter;
        this.flowRepository = flowRepository;
        this.challengeRepository = challengeRepository;
    }

    public void enforceStart(NormalizedIdentifier normalized, String clientIp, String clientFingerprint, Instant now) {
        AuthProperties.AuthStart authStart = properties.getRateLimit().getAuthStart();
        rateLimiter.checkAndIncrement(
                RateLimitScope.AUTH_START_IDENTIFIER,
                normalized.type() + ":" + normalized.value(),
                authStart.getIdentifierWindowSeconds(),
                authStart.getIdentifierMaxAttempts()
        );
        rateLimiter.checkAndIncrement(
                RateLimitScope.AUTH_START_IP,
                clientIp,
                authStart.getIpWindowSeconds(),
                authStart.getIpMaxAttempts()
        );
        rateLimiter.checkAndIncrement(
                RateLimitScope.AUTH_START_DEVICE,
                clientFingerprint,
                authStart.getIpWindowSeconds(),
                authStart.getIpMaxAttempts()
        );
        enforceResendCooldown(normalized, now);
    }

    public void enforceOtpAttempt(AuthLoginFlowJpaEntity flow, String clientIp) {
        AuthProperties.OtpVerify otpVerify = properties.getRateLimit().getOtpVerify();
        String flowKey = flow.getId().toString();
        String identifierKey = identifierKey(flow);
        rateLimiter.ensureNotLocked(RateLimitScope.OTP_VERIFY_FLOW, flowKey);
        rateLimiter.ensureNotLocked(RateLimitScope.OTP_VERIFY_IDENTIFIER, identifierKey);
        rateLimiter.checkAndIncrement(
                RateLimitScope.OTP_VERIFY_FLOW,
                flowKey,
                otpVerify.getFlowWindowSeconds(),
                otpVerify.getFlowMaxAttempts()
        );
        rateLimiter.checkAndIncrement(
                RateLimitScope.OTP_VERIFY_IDENTIFIER,
                identifierKey,
                otpVerify.getFlowWindowSeconds(),
                otpVerify.getFlowMaxAttempts()
        );
        rateLimiter.checkAndIncrement(
                RateLimitScope.OTP_VERIFY_IP,
                clientIp,
                otpVerify.getIpWindowSeconds(),
                otpVerify.getIpMaxAttempts()
        );
    }

    public void softLockOtp(AuthLoginFlowJpaEntity flow) {
        long lockoutSeconds = properties.getRateLimit().getLockout().getDurationSeconds();
        rateLimiter.lock(RateLimitScope.OTP_VERIFY_FLOW, flow.getId().toString(), lockoutSeconds);
        rateLimiter.lock(RateLimitScope.OTP_VERIFY_IDENTIFIER, identifierKey(flow), lockoutSeconds);
    }

    private void enforceResendCooldown(NormalizedIdentifier normalized, Instant now) {
        long cooldownSeconds = properties.getRateLimit().getResend().getCooldownSeconds();
        if (cooldownSeconds <= 0) {
            return;
        }
        flowRepository.findTopByIdentifierTypeAndNormalizedIdentifierAndStatusOrderByCreatedAtDesc(
                        normalized.type(),
                        normalized.value(),
                        LoginFlowStatus.OTP_REQUIRED
                )
                .flatMap(flow -> challengeRepository.findTopByFlowIdOrderByCreatedAtDesc(flow.getId()))
                .filter(challenge -> challenge.getStatus() == OtpChallengeStatus.ACTIVE)
                .filter(challenge -> challenge.getExpiresAt().isAfter(now))
                .map(AuthOtpChallengeJpaEntity::getCreatedAt)
                .map(createdAt -> createdAt.plusSeconds(cooldownSeconds))
                .filter(cooldownUntil -> cooldownUntil.isAfter(now))
                .ifPresent(cooldownUntil -> {
                    throw new RateLimitExceededException(cooldownUntil.getEpochSecond() - now.getEpochSecond());
                });
    }

    private String identifierKey(AuthLoginFlowJpaEntity flow) {
        return flow.getIdentifierType() + ":" + flow.getNormalizedIdentifier();
    }
}
