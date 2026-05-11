package app.viaverse.identity.auth.application.usecase;

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
import app.viaverse.identity.shared.logging.ActionLogContext;
import app.viaverse.identity.shared.logging.ObservedAction;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import app.viaverse.observability.logging.SafeLogFields;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StartAuthUseCase {
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

    @ObservedAction("auth.start")
    @Transactional(noRollbackFor = RateLimitExceededException.class)
    public StartAuthResponse start(String identifier, String clientIp, String clientFingerprint) {
        Instant now = Instant.now();
        NormalizedIdentifier normalized = identifierNormalizer.normalize(identifier);
        ActionLogContext.put("auth.identifier_type", normalized.type());
        ActionLogContext.put("auth.identifier_masked", SafeLogFields.maskIdentifier(normalized.value()));
        abuseProtectionService.enforceStart(normalized, clientIp, clientFingerprint, now);

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
        ActionLogContext.put("auth.flow_id", flow.getId());
        return new StartAuthResponse(
                flow.getId(),
                normalized.type(),
                AuthNextStep.OTP_REQUIRED,
                expiresAt,
                debugOtp
        );
    }
}
