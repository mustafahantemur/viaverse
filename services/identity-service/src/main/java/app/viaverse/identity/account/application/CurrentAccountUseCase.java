package app.viaverse.identity.account.application;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.account.infrastructure.persistence.entity.IdentityAccountJpaEntity;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthSessionJpaEntity;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentAccountUseCase {
    private final AuthSessionIssuer sessionIssuer;

    public CurrentAccountUseCase(AuthSessionIssuer sessionIssuer) {
        this.sessionIssuer = sessionIssuer;
    }

    @Transactional(readOnly = true)
    public AccountView currentAccount(String authorizationHeader) {
        JwtPrincipal principal = sessionIssuer.authenticate(authorizationHeader);
        AuthSessionJpaEntity session = sessionIssuer.activeSession(principal.sessionId(), Instant.now());
        IdentityAccountJpaEntity account = sessionIssuer.activeAccount(session.getAccountId());
        return sessionIssuer.accountView(account);
    }
}
