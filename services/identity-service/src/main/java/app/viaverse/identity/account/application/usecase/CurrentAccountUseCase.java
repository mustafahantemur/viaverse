package app.viaverse.identity.account.application.usecase;

import app.viaverse.identity.account.domain.AccountView;
import app.viaverse.identity.account.infrastructure.persistence.entity.IdentityAccountJpaEntity;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.infrastructure.persistence.entity.AuthSessionJpaEntity;
import app.viaverse.identity.auth.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.shared.logging.ActionLogContext;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentAccountUseCase {
    private final AuthSessionIssuer sessionIssuer;

    public CurrentAccountUseCase(AuthSessionIssuer sessionIssuer) {
        this.sessionIssuer = sessionIssuer;
    }

    @ObservedAction("account.current")
    @Transactional(readOnly = true)
    public AccountView currentAccount(JwtPrincipal principal) {
        AuthSessionJpaEntity session = sessionIssuer.activeSession(principal.sessionId(), Instant.now());
        ActionLogContext.put("auth.session_id", session.getId());
        IdentityAccountJpaEntity account = sessionIssuer.activeAccount(session.getAccountId());
        ActionLogContext.put("user.id", account.getId());
        return sessionIssuer.accountView(account);
    }
}
