package app.viaverse.identity.infrastructure.security;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class SecureTokenGenerator {
    private final SecureRandom secureRandom;

    public SecureTokenGenerator(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String generateUrlToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
