package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.application.port.out.RateLimitPort;
import app.viaverse.identity.auth.domain.enums.RateLimitScopeEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class AuthAbuseProtectionService {

    private final AuthProperties properties;
    private final RateLimitPort rateLimit;

    public AuthAbuseProtectionService(AuthProperties properties, RateLimitPort rateLimit) {
        this.properties = properties;
        this.rateLimit = rateLimit;
    }

    public void enforceStart(NormalizedIdentifier normalized, String clientIp, String clientFingerprint) {
        AuthProperties.AuthStart authStart = properties.getRateLimit().getAuthStart();
        check(RateLimitScopeEnum.AUTH_START_IDENTIFIER, normalized.type() + ":" + normalized.value(),
                authStart.getIdentifierMaxAttempts(), Duration.ofSeconds(authStart.getIdentifierWindowSeconds()));
        check(RateLimitScopeEnum.AUTH_START_IP, clientIp,
                authStart.getIpMaxAttempts(), Duration.ofSeconds(authStart.getIpWindowSeconds()));
        check(RateLimitScopeEnum.AUTH_START_DEVICE, clientFingerprint,
                authStart.getDeviceMaxAttempts(), Duration.ofSeconds(authStart.getDeviceWindowSeconds()));
        enforceResendCooldown(normalized);
    }

    public void enforceSocialStart(NormalizedIdentifier normalized, String clientIp, String clientFingerprint) {
        AuthProperties.AuthStart authStart = properties.getRateLimit().getAuthStart();
        check(RateLimitScopeEnum.AUTH_START_IDENTIFIER, normalized.type() + ":" + normalized.value(),
                authStart.getIdentifierMaxAttempts(), Duration.ofSeconds(authStart.getIdentifierWindowSeconds()));
        check(RateLimitScopeEnum.AUTH_START_IP, clientIp,
                authStart.getIpMaxAttempts(), Duration.ofSeconds(authStart.getIpWindowSeconds()));
        check(RateLimitScopeEnum.AUTH_START_DEVICE, clientFingerprint,
                authStart.getDeviceMaxAttempts(), Duration.ofSeconds(authStart.getDeviceWindowSeconds()));
    }

    public void enforceOtpAttempt(AuthLoginFlow flow, String clientIp) {
        AuthProperties.OtpVerify otpVerify = properties.getRateLimit().getOtpVerify();
        String flowKey = flow.getId().toString();
        String identifierKey = flow.getIdentifierType() + ":" + flow.getNormalizedIdentifier();
        check(RateLimitScopeEnum.OTP_VERIFY_FLOW, flowKey,
                otpVerify.getFlowMaxAttempts(), Duration.ofSeconds(otpVerify.getFlowWindowSeconds()));
        check(RateLimitScopeEnum.OTP_VERIFY_IDENTIFIER, identifierKey,
                otpVerify.getFlowMaxAttempts(), Duration.ofSeconds(otpVerify.getFlowWindowSeconds()));
        check(RateLimitScopeEnum.OTP_VERIFY_IP, clientIp,
                otpVerify.getIpMaxAttempts(), Duration.ofSeconds(otpVerify.getIpWindowSeconds()));
    }

    public void enforceRefresh(String clientIp) {
        AuthProperties.AuthStart cfg = properties.getRateLimit().getAuthStart();
        check(RateLimitScopeEnum.AUTH_START_IP, "refresh:" + clientIp,
                cfg.getIpMaxAttempts(), Duration.ofSeconds(cfg.getIpWindowSeconds()));
    }

    public void enforceLogout(String clientIp) {
        AuthProperties.AuthStart cfg = properties.getRateLimit().getAuthStart();
        check(RateLimitScopeEnum.AUTH_START_IP, "logout:" + clientIp,
                cfg.getIpMaxAttempts(), Duration.ofSeconds(cfg.getIpWindowSeconds()));
    }

    public void softLockOtp(AuthLoginFlow flow) {
        Duration lockout = Duration.ofSeconds(properties.getRateLimit().getLockout().getDurationSeconds());
        check(RateLimitScopeEnum.OTP_VERIFY_FLOW, flow.getId().toString(), 0, lockout);
        check(RateLimitScopeEnum.OTP_VERIFY_IDENTIFIER,
                flow.getIdentifierType() + ":" + flow.getNormalizedIdentifier(), 0, lockout);
    }

    private void enforceResendCooldown(NormalizedIdentifier normalized) {
        long cooldownSeconds = properties.getRateLimit().getResend().getCooldownSeconds();
        if (cooldownSeconds <= 0) {
            return;
        }
        RateLimitPort.Result result = rateLimit.incrementAndCheck(
                RateLimitScopeEnum.AUTH_START_IDENTIFIER,
                "resend:" + normalized.type() + ":" + normalized.value(),
                1,
                Duration.ofSeconds(cooldownSeconds)
        );
        if (!result.allowed()) {
            throw new RateLimitExceededException(result.ttlSeconds());
        }
    }

    private void check(RateLimitScopeEnum scope, String key, int limit, Duration window) {
        RateLimitPort.Result result = rateLimit.incrementAndCheck(scope, key, limit, window);
        if (!result.allowed()) {
            throw new RateLimitExceededException(result.ttlSeconds());
        }
    }
}
