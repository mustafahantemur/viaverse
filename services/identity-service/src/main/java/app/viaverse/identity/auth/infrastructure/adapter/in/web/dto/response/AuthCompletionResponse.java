package app.viaverse.identity.auth.infrastructure.adapter.in.web.dto.response;

/**
 * Sealed response type returned by any auth flow that can terminate either with
 * a fully authenticated session ({@link AuthResponse}) or a registration handoff
 * ({@link RegistrationRequiredResponse}). Shared by OTP verification and social
 * sign-in.
 */
public sealed interface AuthCompletionResponse permits AuthResponse, RegistrationRequiredResponse {
}
