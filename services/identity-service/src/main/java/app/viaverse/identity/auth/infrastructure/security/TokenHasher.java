package app.viaverse.identity.auth.infrastructure.security;

import app.viaverse.identity.shared.error.IdentityErrors;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TokenHasher {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final byte[] secret;

    public TokenHasher(String secret) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw IdentityErrors.jwtSecretTooWeak();
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String hash(String value) {
        if (value == null) {
            throw IdentityErrors.tokenHashFailed(new IllegalArgumentException("Token value is required"));
        }
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw IdentityErrors.tokenHashFailed(exception);
        }
    }

    public boolean matches(String rawValue, String expectedHash) {
        if (rawValue == null || expectedHash == null) {
            return false;
        }
        return MessageDigest.isEqual(
                hash(rawValue).getBytes(StandardCharsets.UTF_8),
                expectedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}
