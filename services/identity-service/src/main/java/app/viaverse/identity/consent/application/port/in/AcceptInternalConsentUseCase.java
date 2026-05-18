package app.viaverse.identity.consent.application.port.in;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.shared.aspect.AuditableCommand;
import java.util.UUID;

public interface AcceptInternalConsentUseCase {

    Result execute(Command command);

    record Command(
            UUID accountId,
            ConsentTypeEnum type,
            String version,
            String source
    ) implements AuditableCommand {
    }

    record Result(UUID accountId, ConsentTypeEnum type, String version, boolean created) {
    }
}
