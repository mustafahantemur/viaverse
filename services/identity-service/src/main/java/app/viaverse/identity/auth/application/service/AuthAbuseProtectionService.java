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
        checkDeviceIfPresent(authStart, clientFingerprint);
        // NOTE: the resend cooldown is intentionally NOT enforced here. /auth/start
        // is a triage call — it may return PASSWORD_REQUIRED (known identifier, no
        // OTP sent) and applying the 1-per-60s cooldown to that path blocks
        // legitimate quick re-tries (user double-clicks, types two identifiers
        // back-to-back, etc.). Call {@link #enforceOtpResendCooldown} explicitly
        // from the branches that actually dispatch an OTP.
    }

    /**
     * Cooldown that protects the OTP dispatch channel from spam. Apply this
     * right before issuing an OTP — not in {@link #enforceStart}, which would
     * incorrectly throttle the "known identifier → password screen" path.
     */
    public void enforceOtpResendCooldown(NormalizedIdentifier normalized) {
        enforceResendCooldown(normalized);
    }

    public void enforceSocialStart(NormalizedIdentifier normalized, String clientIp, String clientFingerprint) {
        AuthProperties.AuthStart authStart = properties.getRateLimit().getAuthStart();
        check(RateLimitScopeEnum.AUTH_START_IDENTIFIER, normalized.type() + ":" + normalized.value(),
                authStart.getIdentifierMaxAttempts(), Duration.ofSeconds(authStart.getIdentifierWindowSeconds()));
        check(RateLimitScopeEnum.AUTH_START_IP, clientIp,
                authStart.getIpMaxAttempts(), Duration.ofSeconds(authStart.getIpWindowSeconds()));
        checkDeviceIfPresent(authStart, clientFingerprint);
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

    public void enforcePasswordLogin(NormalizedIdentifier normalized, String clientIp) {
        AuthProperties.PasswordLogin cfg = properties.getRateLimit().getPasswordLogin();
        check(RateLimitScopeEnum.PASSWORD_LOGIN_IDENTIFIER,
                normalized.type() + ":" + normalized.value(),
                cfg.getIdentifierMaxAttempts(),
                Duration.ofSeconds(cfg.getIdentifierWindowSeconds()));
        check(RateLimitScopeEnum.PASSWORD_LOGIN_IP, clientIp,
                cfg.getIpMaxAttempts(),
                Duration.ofSeconds(cfg.getIpWindowSeconds()));
    }

    public void enforceTotpVerify(java.util.UUID accountId, String clientIp) {
        AuthProperties.PasswordLogin cfg = properties.getRateLimit().getPasswordLogin();
        check(RateLimitScopeEnum.TOTP_VERIFY_ACCOUNT, accountId.toString(),
                cfg.getIdentifierMaxAttempts(),
                Duration.ofSeconds(cfg.getIdentifierWindowSeconds()));
        check(RateLimitScopeEnum.TOTP_VERIFY_IP, clientIp,
                cfg.getIpMaxAttempts(),
                Duration.ofSeconds(cfg.getIpWindowSeconds()));
    }

    public void enforceTwoFactorOp(java.util.UUID accountId) {
        AuthProperties.PasswordLogin cfg = properties.getRateLimit().getPasswordLogin();
        check(RateLimitScopeEnum.TWO_FACTOR_OP_ACCOUNT, accountId.toString(),
                cfg.getIdentifierMaxAttempts(),
                Duration.ofSeconds(cfg.getIdentifierWindowSeconds()));
    }

    public void softLockOtp(AuthLoginFlow flow) {
        Duration lockout = Duration.ofSeconds(properties.getRateLimit().getLockout().getDurationSeconds());
        primeLockout(RateLimitScopeEnum.OTP_VERIFY_FLOW, flow.getId().toString(), lockout);
        primeLockout(
                RateLimitScopeEnum.OTP_VERIFY_IDENTIFIER,
                flow.getIdentifierType() + ":" + flow.getNormalizedIdentifier(),
                lockout
        );
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

    private void checkDeviceIfPresent(AuthProperties.AuthStart authStart, String clientFingerprint) {
        if (clientFingerprint == null || clientFingerprint.isBlank()) {
            return;
        }
        check(RateLimitScopeEnum.AUTH_START_DEVICE, clientFingerprint,
                authStart.getDeviceMaxAttempts(), Duration.ofSeconds(authStart.getDeviceWindowSeconds()));
    }

    private void check(RateLimitScopeEnum scope, String key, int limit, Duration window) {
        RateLimitPort.Result result = rateLimit.incrementAndCheck(scope, key, limit, window);
        if (!result.allowed()) {
            throw new RateLimitExceededException(result.ttlSeconds());
        }
    }

    private void primeLockout(RateLimitScopeEnum scope, String key, Duration window) {
        rateLimit.incrementAndCheck(scope, key, 0, window);
    }
}
