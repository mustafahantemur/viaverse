package app.viaverse.identity.auth.application.usecase;

import app.viaverse.identity.auth.application.port.in.IssueAdminInvitationUseCase;
import app.viaverse.identity.auth.application.service.AdminInvitationService;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import org.springframework.stereotype.Service;

@Service
public class IssueAdminInvitationUseCaseImpl implements IssueAdminInvitationUseCase {
    private final Clock clock;
    private final AdminInvitationService adminInvitationService;

    public IssueAdminInvitationUseCaseImpl(
            Clock clock,
            AdminInvitationService adminInvitationService
    ) {
        this.clock = clock;
        this.adminInvitationService = adminInvitationService;
    }

    @Override
    @ObservedAction("admin.invitation.issue")
    public Result execute(Command command) {
        AdminInvitationService.Issued issued =
                adminInvitationService.issue(command.issuedByAccountId(), clock.instant());
        return new Result(issued.token(), issued.expiresAt());
    }
}

