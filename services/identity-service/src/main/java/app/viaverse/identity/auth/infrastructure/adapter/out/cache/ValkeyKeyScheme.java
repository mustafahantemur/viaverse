package app.viaverse.identity.auth.infrastructure.adapter.out.cache;

import app.viaverse.identity.auth.domain.enums.RateLimitScopeEnum;
import java.util.UUID;

public final class ValkeyKeyScheme {

    private ValkeyKeyScheme() {}

    public static String otp(UUID flowId) {
        return "otp:" + flowId;
    }

    public static String registrationToken(String tokenHash) {
        return "reg:" + tokenHash;
    }

    public static String rateLimit(RateLimitScopeEnum scope, String keyHash) {
        return "rl:" + scope.name().toLowerCase() + ":" + keyHash;
    }

    public static String session(UUID sessionId) {
        return "session:" + sessionId;
    }
}
