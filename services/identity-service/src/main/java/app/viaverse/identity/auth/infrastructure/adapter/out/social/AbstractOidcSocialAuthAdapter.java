package app.viaverse.identity.auth.infrastructure.adapter.out.social;

import app.viaverse.identity.auth.application.port.out.SocialAuthPort;
import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import app.viaverse.identity.auth.domain.value.SocialIdentity;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.net.URL;
import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

/**
 * Base validator for OIDC providers (Google, Apple) that return ID tokens
 * signed with the provider's published JWKS. Verifies, in order:
 *
 * <ol>
 *   <li>JWS signature against the provider's JWKS (delegated to {@link JwtDecoder}).</li>
 *   <li>{@code iss} matches one of {@link #acceptedIssuers}.</li>
 *   <li>{@code aud} contains our configured client id.</li>
 *   <li>{@code nonce} matches the value the client just generated and pinned
 *       into its request. The nonce binds the ID token to this exact sign-in
 *       attempt; the server treats it as opaque and trusts the client to
 *       generate it freshly. Replay protection comes from the short
 *       {@code AuthLoginFlow} TTL — a stolen ID token cannot be replayed
 *       after that window expires.</li>
 *   <li>{@code sub} is present (the canonical provider user id).</li>
 * </ol>
 *
 * <p>{@code iat} / {@code exp} / {@code nbf} are validated upstream by the
 * {@link JwtDecoder}'s default validator (configured in the concrete subclass).
 */
abstract class AbstractOidcSocialAuthAdapter implements SocialAuthPort {

    private final SocialAuthProviderEnum provider;
    private final String clientId;
    private final List<String> acceptedIssuers;
    private final JwtDecoder jwtDecoder;

    AbstractOidcSocialAuthAdapter(
            SocialAuthProviderEnum provider,
            String clientId,
            List<String> acceptedIssuers,
            JwtDecoder jwtDecoder
    ) {
        this.provider = provider;
        this.clientId = clientId;
        this.acceptedIssuers = List.copyOf(acceptedIssuers);
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public final boolean supports(SocialAuthProviderEnum candidate) {
        return provider == candidate;
    }

    @Override
    public SocialIdentity verify(String idToken, String expectedNonce) {
        try {
            Jwt jwt = jwtDecoder.decode(idToken);
            validateIssuer(jwt);
            validateAudience(jwt);
            validateNonce(jwt, expectedNonce);
            validateSubject(jwt);
            return new SocialIdentity(
                    provider,
                    jwt.getSubject(),
                    jwt.getClaimAsString("email"),
                    emailVerified(jwt)
            );
        } catch (JwtException exception) {
            throw IdentityErrors.invalidSocialToken();
        }
    }

    private void validateIssuer(Jwt jwt) {
        URL issuer = jwt.getIssuer();
        if (issuer == null || !acceptedIssuers.contains(issuer.toString())) {
            throw IdentityErrors.invalidSocialToken();
        }
    }

    private void validateAudience(Jwt jwt) {
        List<String> audience = jwt.getAudience();
        if (audience == null || !audience.contains(clientId)) {
            throw IdentityErrors.invalidSocialToken();
        }
    }

    private void validateNonce(Jwt jwt, String expectedNonce) {
        if (expectedNonce == null || expectedNonce.isBlank()) {
            throw IdentityErrors.invalidSocialToken();
        }
        String actualNonce = jwt.getClaimAsString("nonce");
        if (actualNonce == null || !constantTimeEquals(expectedNonce, actualNonce)) {
            throw IdentityErrors.invalidSocialToken();
        }
    }

    private void validateSubject(Jwt jwt) {
        if (jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw IdentityErrors.invalidSocialToken();
        }
    }

    private boolean emailVerified(Jwt jwt) {
        Object claim = jwt.getClaims().get("email_verified");
        if (claim instanceof Boolean bool) {
            return bool;
        }
        if (claim instanceof String text) {
            return Boolean.parseBoolean(text);
        }
        return false;
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
