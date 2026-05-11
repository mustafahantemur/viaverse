package app.viaverse.identity.auth.infrastructure.security;

import app.viaverse.shared.kernel.error.TechnicalException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TokenHasher {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final byte[] secret;

    public TokenHasher(String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String hash(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new TechnicalException("Unable to hash token", exception);
        }
    }

    public boolean matches(String rawValue, String expectedHash) {
        return MessageDigest.isEqual(
                hash(rawValue).getBytes(StandardCharsets.UTF_8),
                expectedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}
