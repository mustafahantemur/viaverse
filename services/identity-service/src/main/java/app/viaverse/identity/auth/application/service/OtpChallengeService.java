package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
import app.viaverse.identity.auth.domain.enums.OtpChallengeStatus;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import app.viaverse.identity.auth.infrastructure.otp.OtpService;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthLoginFlowJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthOtpChallengeJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthOtpChallengeJpaRepository;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OtpChallengeService {
    private final AuthProperties properties;
    private final OtpService otpService;
    private final TokenHasher tokenHasher;
    private final AuthOtpChallengeJpaRepository challengeRepository;

    public OtpChallengeService(
            AuthProperties properties,
            OtpService otpService,
            TokenHasher tokenHasher,
            AuthOtpChallengeJpaRepository challengeRepository
    ) {
        this.properties = properties;
        this.otpService = otpService;
        this.tokenHasher = tokenHasher;
        this.challengeRepository = challengeRepository;
    }

    public String issue(UUID flowId, NormalizedIdentifier normalized, Instant expiresAt, Instant now) {
        String otp = otpService.generate();
        challengeRepository.save(new AuthOtpChallengeJpaEntity(
                UUID.randomUUID(),
                flowId,
                tokenHasher.hash(otp),
                properties.getOtp().getMaxAttempts(),
                expiresAt,
                now
        ));
        otpService.deliver(new OtpDeliveryRequest(flowId, normalized, otp, expiresAt));
        return otpService.debugOtp(otp);
    }

    public AuthOtpChallengeJpaEntity latestChallenge(UUID flowId) {
        return challengeRepository.findTopByFlowIdOrderByCreatedAtDesc(flowId)
                .orElseThrow(() -> IdentityErrors.invalidAuthFlow(Map.of("flowId", "has no OTP")));
    }

    public void validateWaitingForOtp(
            AuthLoginFlowJpaEntity flow,
            AuthOtpChallengeJpaEntity challenge,
            Instant now
    ) {
        if (flow.getStatus() != LoginFlowStatus.OTP_REQUIRED || challenge.getStatus() != OtpChallengeStatus.ACTIVE) {
            throw IdentityErrors.authFlowNotWaitingForOtp();
        }
        if (flow.getExpiresAt().isBefore(now) || challenge.getExpiresAt().isBefore(now)) {
            flow.fail(LoginFlowStatus.EXPIRED, now);
            challenge.expire();
            throw IdentityErrors.otpExpired();
        }
    }

    public boolean matches(AuthOtpChallengeJpaEntity challenge, String otp) {
        return tokenHasher.matches(otp, challenge.getOtpHash());
    }

    public boolean recordFailure(AuthOtpChallengeJpaEntity challenge) {
        challenge.recordFailure();
        return challenge.getStatus() == OtpChallengeStatus.LOCKED;
    }

    public void verify(AuthOtpChallengeJpaEntity challenge, Instant now) {
        challenge.verify(now);
    }
}
