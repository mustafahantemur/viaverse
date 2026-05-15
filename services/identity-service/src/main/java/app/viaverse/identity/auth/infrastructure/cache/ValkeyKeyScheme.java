package app.viaverse.identity.auth.infrastructure.cache;

import app.viaverse.identity.auth.domain.enums.RateLimitScope;
import java.util.UUID;

public final class ValkeyKeyScheme {

    private ValkeyKeyScheme() {}

    public static String otp(UUID flowId) {
        return "otp:" + flowId;
    }

    public static String registrationToken(String tokenHash) {
        return "reg:" + tokenHash;
    }

    public static String rateLimit(RateLimitScope scope, String keyHash) {
        return "rl:" + scope.name().toLowerCase() + ":" + keyHash;
    }

    public static String session(UUID sessionId) {
        return "session:" + sessionId;
    }
}
