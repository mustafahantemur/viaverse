package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.ListSessionsUseCase;
import app.viaverse.identity.auth.application.port.out.AuthSessionRepository;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListSessionsUseCaseImpl implements ListSessionsUseCase {

    private final AuthSessionRepository sessionRepository;

    public ListSessionsUseCaseImpl(AuthSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    @ObservedAction("sessions.list")
    public List<AuthSession> execute(Command command) {
        return sessionRepository.findActiveByAccountId(command.accountId());
    }
}
