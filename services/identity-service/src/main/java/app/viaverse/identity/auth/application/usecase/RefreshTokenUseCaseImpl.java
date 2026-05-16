package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.RefreshTokenUseCase;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.RefreshTokenRotationService;
import app.viaverse.identity.auth.application.service.RefreshTokenRotationService.Rotation;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final Clock clock;
    private final RefreshTokenRotationService rotationService;
    private final AuthSessionIssuer sessionIssuer;

    public RefreshTokenUseCaseImpl(
            Clock clock,
            RefreshTokenRotationService rotationService,
            AuthSessionIssuer sessionIssuer
    ) {
        this.clock = clock;
        this.rotationService = rotationService;
        this.sessionIssuer = sessionIssuer;
    }

    @Override
    @ObservedAction("token.refresh")
    public Result execute(Command command) {
        Instant now = clock.instant();
        Rotation rotation = rotationService.rotate(command.refreshToken(), now);
        AuthSession session = sessionIssuer.activeSession(rotation.sessionId(), now);
        session.touch(now);
        Account account = sessionIssuer.activeAccount(session.getAccountId());
        AuthSessionIssuer.Issued issued = sessionIssuer.issueForExistingSession(
                account, session, rotation.refreshToken(), rotation.refreshTokenExpiresAt(), now);
        return new Result(
                account.getId(),
                session.getId(),
                issued.accessToken(),
                issued.accessTokenExpiresAt(),
                issued.refreshToken(),
                issued.refreshTokenExpiresAt()
        );
    }
}
