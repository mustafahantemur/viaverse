package app.viaverse.identity.account.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

/**
 * Disable 2FA. Caller must provide:
 *  - {@code flowId} + {@code otp}: proof they still control the primary identifier
 *  - <em>either</em> a fresh 6-digit TOTP code from the authenticator app
 *    <em>or</em> one of the single-use backup codes issued at enrollment time.
 *
 * The dual path is the escape hatch for users who lost their phone but still
 * have their backup codes printed somewhere safe.
 */
public record TwoFactorDisableRequest(
        @NotNull UUID flowId,
        @NotBlank @Pattern(regexp = "^\\d{6}$", message = "must be 6 digits") String otp,
        @Pattern(regexp = "^\\d{6}$", message = "must be 6 digits") String totpCode,
        String backupCode
) {
}
