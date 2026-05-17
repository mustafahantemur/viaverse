package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Stage 1 of the new draft-based registration. The client posts the
 * complete form; the server validates everything (password policy,
 * consents, identifier uniqueness), stashes the draft in Valkey, and
 * dispatches an email OTP. The plaintext password leaves memory before
 * the draft is persisted — only the hashed form ever sits in the cache.
 */
public interface StartRegistrationUseCase {

    Result execute(Command command);

    record Command(
            String email,
            String phone,
            String displayName,
            String firstName,
            String lastName,
            String password,
            List<ConsentTypeEnum> acceptedRequiredConsents,
            boolean marketingConsentAccepted,
            String clientIp,
            String clientFingerprint,
            String userAgent
    ) {}

    record Result(
            UUID draftId,
            UUID emailFlowId,
            Instant emailExpiresAt,
            boolean phoneRequiredAfterEmail
    ) {}
}
