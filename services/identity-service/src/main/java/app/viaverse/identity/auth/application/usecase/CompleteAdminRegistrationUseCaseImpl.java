package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.account.domain.AccountRoleEnum;
import app.viaverse.identity.auth.application.port.in.CompleteAdminRegistrationUseCase;
import app.viaverse.identity.auth.application.port.in.CompleteRegistrationUseCase;
import app.viaverse.identity.auth.application.service.AdminInvitationService;
import app.viaverse.identity.auth.application.service.RegistrationCompletionService;
import app.viaverse.identity.shared.audit.AuditEvent;
import app.viaverse.identity.shared.audit.IdentityAuditEventEnum;
import app.viaverse.identity.shared.logging.ObservedAction;
import java.time.Clock;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CompleteAdminRegistrationUseCaseImpl implements CompleteAdminRegistrationUseCase {
    private final Clock clock;
    private final AdminInvitationService adminInvitationService;
    private final RegistrationCompletionService registrationCompletionService;

    public CompleteAdminRegistrationUseCaseImpl(
            Clock clock,
            AdminInvitationService adminInvitationService,
            RegistrationCompletionService registrationCompletionService
    ) {
        this.clock = clock;
        this.adminInvitationService = adminInvitationService;
        this.registrationCompletionService = registrationCompletionService;
    }

    @Override
    @ObservedAction("auth.register_admin")
    @AuditEvent(IdentityAuditEventEnum.ACCOUNT_CREATED)
    public Result execute(Command command) {
        adminInvitationService.consume(command.invitationToken(), clock.instant());
        RegistrationCompletionService.Completed completed = registrationCompletionService.complete(
                new CompleteRegistrationUseCase.Command(
                        command.registrationToken(),
                        command.displayName(),
                        command.firstName(),
                        command.lastName(),
                        command.acceptedRequiredConsents(),
                        command.marketingConsentAccepted(),
                        command.userAgent(),
                        command.clientIp()
                ),
                Set.of(AccountRoleEnum.USER, AccountRoleEnum.ADMIN)
        );
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
