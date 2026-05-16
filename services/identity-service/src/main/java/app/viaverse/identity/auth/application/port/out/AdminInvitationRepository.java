package app.viaverse.identity.auth.application.port.out;

import app.viaverse.identity.auth.domain.model.AdminInvitation;
import java.util.Optional;

public interface AdminInvitationRepository {
    AdminInvitation save(AdminInvitation invitation);

    Optional<AdminInvitation> findByTokenHash(String tokenHash);
}
