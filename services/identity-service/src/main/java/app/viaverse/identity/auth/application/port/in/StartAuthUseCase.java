package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStep;
import app.viaverse.identity.auth.domain.enums.IdentifierType;
import java.time.Instant;
import java.util.UUID;

public interface StartAuthUseCase {

    Result execute(Command command);

    record Command(String identifier, String clientIp, String clientFingerprint) {}

    record Result(
            UUID flowId,
            IdentifierType identifierType,
            AuthNextStep nextStep,
            Instant expiresAt,
            String debugOtp
    ) {}
}
