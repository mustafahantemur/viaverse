package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.auth.infrastructure.adapter.out.cache.ValkeyKeyScheme;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Holds the unconfirmed TOTP secret an authenticated user is enrolling, keyed
 * by account id. We keep it in Valkey rather than {@code identity_account}
 * because (a) an abandoned enrollment shouldn't dirty the canonical row, and
 * (b) we want it to auto-expire — if the user closes the QR-code screen
 * without confirming, the secret is forgotten in 10 minutes.
 */
@Service
public class TwoFactorEnrollmentService {
    public static final Duration PENDING_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redis;

    public TwoFactorEnrollmentService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void savePending(UUID accountId, byte[] rawSecret) {
        if (rawSecret == null || rawSecret.length == 0) {
            throw IdentityErrors.secretEncryptionFailed(new IllegalArgumentException("secret empty"));
        }
        String encoded = Base64.getEncoder().encodeToString(rawSecret);
        redis.opsForValue().set(ValkeyKeyScheme.twoFactorPendingSecret(accountId), encoded, PENDING_TTL);
    }

    public Optional<byte[]> findPending(UUID accountId) {
        String value = redis.opsForValue().get(ValkeyKeyScheme.twoFactorPendingSecret(accountId));
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Base64.getDecoder().decode(value));
    }

    public void clearPending(UUID accountId) {
        redis.delete(ValkeyKeyScheme.twoFactorPendingSecret(accountId));
    }
}
