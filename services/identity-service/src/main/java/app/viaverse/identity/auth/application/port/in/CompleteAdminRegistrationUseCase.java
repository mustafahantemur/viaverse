package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.consent.domain.ConsentInput;
import app.viaverse.identity.shared.aspect.AuditableResult;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CompleteAdminRegistrationUseCase {
    Result execute(Command command);

    record Command(
            String invitationToken,
            String registrationToken,
            String displayName,
            String firstName,
            String lastName,
            List<ConsentInput> requiredConsents,
            boolean marketingConsentAccepted,
            String userAgent,
            String clientIp
    ) {}

    record Result(
            UUID accountId,
            UUID sessionId,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) implements AuditableResult {}
}
