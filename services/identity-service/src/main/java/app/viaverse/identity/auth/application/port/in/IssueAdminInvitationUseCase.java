package app.viaverse.identity.auth.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface IssueAdminInvitationUseCase {
    Result execute(Command command);

    record Command(UUID issuedByAccountId) {}

    record Result(String invitationToken, Instant expiresAt) {}
}
