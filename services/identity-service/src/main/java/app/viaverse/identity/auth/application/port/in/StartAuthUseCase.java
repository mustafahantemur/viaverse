package app.viaverse.identity.auth.application.port.in;

import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import java.time.Instant;
import java.util.UUID;

/**
 * Stage 1 of authentication. Given an identifier, the server decides whether
 * this is a known account (returns {@link AuthNextStepEnum#PASSWORD_REQUIRED}
 * with no flowId) or a new identifier worth onboarding (issues an OTP for
 * ownership proof and returns {@link AuthNextStepEnum#OTP_REQUIRED} with a
 * {@code flowId} + {@code expiresAt}).
 *
 * <p>The server intentionally returns a {@code nextStep} even for known
 * accounts so the client can render the right screen without an additional
 * "does this user exist?" probe. This is OK for our threat model — emails
 * are easily enumerable from public registries anyway and we rely on
 * rate-limiting to deter scraping.
 */
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
