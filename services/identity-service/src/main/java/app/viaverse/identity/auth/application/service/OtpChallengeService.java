package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.application.port.out.OtpChallengeStore;
import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatusEnum;
import app.viaverse.identity.auth.domain.enums.OtpChallengeStatusEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.OtpChallenge;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Issues OTP challenges, delivers them via the configured {@link OtpDeliveryPort},
 * and verifies / records failures against the {@link OtpChallengeStore}.
 * <p>
 * Phase 3B rewrite — no JPA imports; pure ports + domain models.
 */
@Service
public class OtpChallengeService {

    private final AuthProperties properties;
    private final OtpChallengeStore challengeStore;
    private final OtpDeliveryPort deliveryPort;
    private final TokenHasher tokenHasher;
    private final SecureRandom secureRandom;

    public OtpChallengeService(
            AuthProperties properties,
            OtpChallengeStore challengeStore,
            OtpDeliveryPort deliveryPort,
            TokenHasher tokenHasher,
            SecureRandom secureRandom
    ) {
        this.properties = properties;
        this.challengeStore = challengeStore;
        this.deliveryPort = deliveryPort;
        this.tokenHasher = tokenHasher;
        this.secureRandom = secureRandom;
    }

    /**
     * Issue a new OTP challenge for the given flow. Returns the OTP value when debug
     * mode is enabled, otherwise {@code null} (the OTP is only delivered out-of-band).
     */
    public String issue(UUID flowId, NormalizedIdentifier normalized, Instant expiresAt, Instant now) {
        String otp = generateOtp();
        OtpChallenge challenge = OtpChallenge.issue(
                UUID.randomUUID(),
                flowId,
                tokenHasher.hash(otp),
                properties.getOtp().getMaxAttempts(),
                expiresAt,
                now
        );
        challengeStore.save(challenge);
        deliveryPort.deliver(new OtpDeliveryRequest(flowId, normalized, otp, expiresAt));
        return debugOtp(otp);
    }

    public OtpChallenge latestChallenge(UUID flowId) {
        return challengeStore.findByFlowId(flowId)
                .orElseThrow(() -> IdentityErrors.invalidAuthFlow(Map.of("flowId", "has no OTP")));
    }

    /**
     * Verify the supplied flow + challenge is in a state that can accept an OTP attempt.
     * Mutates flow/challenge to terminal states on expiry.
     */
    public void validateWaitingForOtp(AuthLoginFlow flow, OtpChallenge challenge, Instant now) {
        if (flow.getStatus() != LoginFlowStatusEnum.OTP_REQUIRED || challenge.getStatus() != OtpChallengeStatusEnum.ACTIVE) {
            throw IdentityErrors.authFlowNotWaitingForOtp();
        }
        if (flow.getExpiresAt().isBefore(now) || challenge.getExpiresAt().isBefore(now)) {
            flow.fail(LoginFlowStatusEnum.EXPIRED, now);
            challenge.expire();
            throw IdentityErrors.otpExpired();
        }
    }

    public boolean matches(OtpChallenge challenge, String otp) {
        return tokenHasher.matches(otp, challenge.getOtpHash());
    }

    /**
     * Record a failed attempt. Returns {@code true} if the challenge transitioned to
     * {@code LOCKED} as a result.
     */
    public boolean recordFailure(OtpChallenge challenge) {
        challenge.recordFailure();
        return challenge.getStatus() == OtpChallengeStatusEnum.LOCKED;
    }

    public void verify(OtpChallenge challenge, Instant now) {
        challenge.verify(now);
    }

    private String generateOtp() {
        if (properties.getDebug().isEnabled()) {
            return properties.getDebug().getFixedOtp();
        }
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private String debugOtp(String otp) {
        return properties.getDebug().isEnabled() ? otp : null;
    }

}
