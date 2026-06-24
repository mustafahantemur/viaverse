package app.viaverse.identity.account.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Authenticated password change / set. {@code currentPassword} is optional —
 * it may be omitted when the account has no password yet (social-only sign-up).
 * Server-side {@code ChangePasswordUseCase} enforces that it is provided when
 * the account already has a password.
 */
public record ChangePasswordRequest(
        String currentPassword,
        @NotBlank String newPassword
) {
}
