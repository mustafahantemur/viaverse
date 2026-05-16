package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.shared.aspect.AuditableResult;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CompleteRegistrationUseCase {

    Result execute(Command command);

    record Command(
            String registrationToken,
            String displayName,
            String firstName,
            String lastName,
            List<ConsentTypeEnum> acceptedRequiredConsents,
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
