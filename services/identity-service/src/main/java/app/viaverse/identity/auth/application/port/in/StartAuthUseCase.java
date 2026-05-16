package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import java.time.Instant;
import java.util.UUID;

public interface StartAuthUseCase {

    Result execute(Command command);

    record Command(String identifier, String clientIp, String clientFingerprint) {}

    record Result(
            UUID flowId,
            IdentifierTypeEnum identifierType,
            AuthNextStepEnum nextStep,
            Instant expiresAt
    ) {}
}
