package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.api.dto.RegistrationRequiredResponse;
import app.viaverse.identity.auth.domain.enums.AuthNextStep;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthLoginFlowJpaEntity;
import app.viaverse.identity.auth.infrastructure.persistence.repository.AuthLoginFlowJpaRepository;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class RegistrationTokenService {
    private final AuthProperties properties;
    private final SecureTokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;
    private final AuthLoginFlowJpaRepository flowRepository;

    public RegistrationTokenService(
            AuthProperties properties,
            SecureTokenGenerator tokenGenerator,
            TokenHasher tokenHasher,
            AuthLoginFlowJpaRepository flowRepository
    ) {
        this.properties = properties;
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
        this.flowRepository = flowRepository;
    }

    public RegistrationRequiredResponse requireRegistration(AuthLoginFlowJpaEntity flow, Instant now) {
        String registrationToken = tokenGenerator.generateUrlToken();
        Instant registrationExpiresAt = now.plus(properties.getOtp().getTtl());
        flow.requireRegistration(tokenHasher.hash(registrationToken), registrationExpiresAt, now);
        return new RegistrationRequiredResponse(
                AuthNextStep.REGISTRATION_REQUIRED,
                registrationToken,
                registrationExpiresAt
        );
    }

    public AuthLoginFlowJpaEntity consumeRegistrationToken(String registrationToken, Instant now) {
        AuthLoginFlowJpaEntity flow = flowRepository.findByRegistrationTokenHash(tokenHasher.hash(registrationToken))
                .orElseThrow(IdentityErrors::invalidRegistrationToken);
        if (flow.getStatus() != LoginFlowStatus.REGISTRATION_REQUIRED
                || flow.getRegistrationExpiresAt() == null
                || flow.getRegistrationExpiresAt().isBefore(now)) {
            throw IdentityErrors.registrationTokenExpired();
        }
        return flow;
    }
}
