package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.RegistrationTokenStore;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatus;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class RegistrationTokenService {

    private final AuthProperties properties;
    private final SecureTokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;
    private final AuthLoginFlowRepository flowRepository;
    private final RegistrationTokenStore registrationTokenStore;

    public RegistrationTokenService(
            AuthProperties properties,
            SecureTokenGenerator tokenGenerator,
            TokenHasher tokenHasher,
            AuthLoginFlowRepository flowRepository,
            RegistrationTokenStore registrationTokenStore
    ) {
        this.properties = properties;
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
        this.flowRepository = flowRepository;
        this.registrationTokenStore = registrationTokenStore;
    }

    public Issued requireRegistration(AuthLoginFlow flow, Instant now) {
        String registrationToken = tokenGenerator.generateUrlToken();
        Duration ttl = properties.getOtp().getTtl();
        Instant registrationExpiresAt = now.plus(ttl);
        String hashed = tokenHasher.hash(registrationToken);
        flow.requireRegistration(hashed, registrationExpiresAt, now);
        flowRepository.save(flow);
        registrationTokenStore.save(hashed, flow.getId(), ttl);
        return new Issued(registrationToken, registrationExpiresAt);
    }

    public AuthLoginFlow consumeRegistrationToken(String registrationToken, Instant now) {
        if (registrationToken == null || registrationToken.isBlank()) {
            throw IdentityErrors.invalidRegistrationToken();
        }
        String hashed = tokenHasher.hash(registrationToken);
        java.util.UUID flowId = registrationTokenStore.findFlowId(hashed)
                .orElseThrow(IdentityErrors::registrationTokenExpired);
        AuthLoginFlow flow = flowRepository.findById(flowId)
                .orElseThrow(IdentityErrors::invalidRegistrationToken);
        if (flow.getStatus() != LoginFlowStatus.REGISTRATION_REQUIRED
                || flow.getRegistrationExpiresAt() == null
                || flow.getRegistrationExpiresAt().isBefore(now)) {
            throw IdentityErrors.registrationTokenExpired();
        }
        registrationTokenStore.delete(hashed);
        return flow;
    }

    public record Issued(String registrationToken, Instant expiresAt) {
    }
}
