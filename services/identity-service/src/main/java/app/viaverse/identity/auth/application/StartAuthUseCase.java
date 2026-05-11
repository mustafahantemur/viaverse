package app.viaverse.identity.auth.application;

import app.viaverse.identity.auth.api.dto.StartAuthResponse;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.OtpChallengeService;
import app.viaverse.identity.auth.domain.enums.AuthNextStep;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthLoginFlowJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.entity.IdentityIdentifierJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthLoginFlowJpaRepository;
import app.viaverse.identity.auth.infrastructure.persistence.repository.IdentityIdentifierJpaRepository;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.RateLimitExceededException;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import app.viaverse.observability.logging.SafeLogFields;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StartAuthUseCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartAuthUseCase.class);

    private final AuthProperties properties;
    private final IdentifierNormalizer identifierNormalizer;
    private final AuthAbuseProtectionService abuseProtectionService;
    private final OtpChallengeService otpChallengeService;
    private final IdentityIdentifierJpaRepository identifierRepository;
    private final AuthLoginFlowJpaRepository flowRepository;

    public StartAuthUseCase(
            AuthProperties properties,
            IdentifierNormalizer identifierNormalizer,
            AuthAbuseProtectionService abuseProtectionService,
            OtpChallengeService otpChallengeService,
            IdentityIdentifierJpaRepository identifierRepository,
            AuthLoginFlowJpaRepository flowRepository
    ) {
        this.properties = properties;
        this.identifierNormalizer = identifierNormalizer;
        this.abuseProtectionService = abuseProtectionService;
        this.otpChallengeService = otpChallengeService;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
    }

    @Transactional(noRollbackFor = RateLimitExceededException.class)
    public StartAuthResponse start(String identifier, String clientIp, String clientFingerprint) {
        Instant now = Instant.now();
        NormalizedIdentifier normalized = identifierNormalizer.normalize(identifier);
        LOGGER.atInfo()
                .addKeyValue("event.action", "auth.start")
                .addKeyValue("event.outcome", "requested")
                .addKeyValue("auth.identifier_type", normalized.type())
                .addKeyValue("auth.identifier_masked", SafeLogFields.maskIdentifier(normalized.value()))
                .log("auth.start requested");
        try {
            abuseProtectionService.enforceStart(normalized, clientIp, clientFingerprint, now);
        } catch (RateLimitExceededException exception) {
            LOGGER.atWarn()
                    .addKeyValue("event.action", "auth.start")
                    .addKeyValue("event.outcome", "rate_limited")
                    .addKeyValue("auth.identifier_type", normalized.type())
                    .addKeyValue("auth.identifier_masked", SafeLogFields.maskIdentifier(normalized.value()))
                    .addKeyValue("retry_after_seconds", exception.retryAfterSeconds())
                    .log("auth.start rate_limited");
            throw exception;
        }

        UUID accountId = identifierRepository.findByIdentifierTypeAndNormalizedIdentifier(
                        normalized.type(),
                        normalized.value()
                )
                .map(IdentityIdentifierJpaEntity::getAccountId)
                .orElse(null);

        Instant expiresAt = now.plus(properties.getOtp().getTtl());
        AuthLoginFlowJpaEntity flow = flowRepository.save(new AuthLoginFlowJpaEntity(
                UUID.randomUUID(),
                normalized.type(),
                normalized.value(),
                accountId,
                LoginFlowStatus.OTP_REQUIRED,
                expiresAt,
                now
        ));
        String debugOtp = otpChallengeService.issue(flow.getId(), normalized, expiresAt, now);
        LOGGER.atInfo()
                .addKeyValue("event.action", "auth.start")
                .addKeyValue("event.outcome", "success")
                .addKeyValue("auth.flow_id", flow.getId())
                .addKeyValue("auth.identifier_type", normalized.type())
                .log("auth.start succeeded");
        return new StartAuthResponse(
                flow.getId(),
                normalized.type(),
                AuthNextStep.OTP_REQUIRED,
                expiresAt,
                debugOtp
        );
    }
}
