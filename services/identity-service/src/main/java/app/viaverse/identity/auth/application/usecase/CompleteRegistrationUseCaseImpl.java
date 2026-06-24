package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.AccountRoleEnum;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.service.RegistrationCompletionService;
import app.viaverse.web.logging.ObservedAction;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CompleteRegistrationUseCaseImpl implements CompleteRegistrationUseCase {

    private final RegistrationCompletionService registrationCompletionService;

    public CompleteRegistrationUseCaseImpl(RegistrationCompletionService registrationCompletionService) {
        this.registrationCompletionService = registrationCompletionService;
    }

    @Override
    @ObservedAction("auth.register")
    @AuditEvent(IdentityAuditEventEnum.ACCOUNT_CREATED)
    public Result execute(Command command) {
        RegistrationCompletionService.Completed completed =
                registrationCompletionService.complete(command, Set.of(AccountRoleEnum.USER));
        var issued = completed.issued();
        return new Result(
                completed.accountId(),
                issued.session().getId(),
                issued.accessToken(),
                issued.accessTokenExpiresAt(),
                issued.refreshToken(),
                issued.refreshTokenExpiresAt()
        );
    }
}

