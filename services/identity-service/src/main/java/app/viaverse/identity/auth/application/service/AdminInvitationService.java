package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.application.port.out.AdminInvitationRepository;
import app.viaverse.identity.auth.domain.model.AdminInvitation;
import app.viaverse.identity.auth.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AdminInvitationService {
    private final AuthProperties properties;
    private final SecureTokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;
    private final AdminInvitationRepository repository;

    public AdminInvitationService(
            AuthProperties properties,
            SecureTokenGenerator tokenGenerator,
            TokenHasher tokenHasher,
            AdminInvitationRepository repository
    ) {
        this.properties = properties;
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
        this.repository = repository;
    }

    public Issued issue(UUID issuedByAccountId, Instant now) {
        String rawToken = tokenGenerator.generateUrlToken();
        AdminInvitation invitation = repository.save(AdminInvitation.issue(
                UUID.randomUUID(),
                tokenHasher.hash(rawToken),
                issuedByAccountId,
                now.plus(properties.getAdminInvitation().getTtl()),
                now
        ));
        return new Issued(rawToken, invitation.getExpiresAt());
    }

    public AdminInvitation consume(String rawToken, Instant now) {
        AdminInvitation invitation = repository.findByTokenHash(tokenHasher.hash(rawToken))
                .orElseThrow(IdentityErrors::invalidAdminInvitationToken);
        if (invitation.isConsumed()) {
            throw IdentityErrors.invalidAdminInvitationToken();
        }
        if (invitation.isExpired(now)) {
            throw IdentityErrors.adminInvitationTokenExpired();
        }
        invitation.consume(now);
        return repository.save(invitation);
    }

    public record Issued(String token, Instant expiresAt) {}
}
