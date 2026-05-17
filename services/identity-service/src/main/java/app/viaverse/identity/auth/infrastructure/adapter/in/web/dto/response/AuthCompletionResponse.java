package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

/**
 * Sealed response type returned by any auth flow that can terminate in one
 * of several states: a fully authenticated session ({@link AuthResponse}),
 * a registration handoff ({@link RegistrationRequiredResponse}), or a 2FA
 * challenge ({@link TotpRequiredResponse}) when the account has TOTP enabled.
 */
public sealed interface AuthCompletionResponse
        permits AuthResponse, RegistrationRequiredResponse, TotpRequiredResponse {
}
