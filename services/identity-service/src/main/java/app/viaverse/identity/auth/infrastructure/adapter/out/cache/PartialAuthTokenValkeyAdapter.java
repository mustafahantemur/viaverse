package app.viaverse.identity.auth.infrastructure.adapter.out.cache;

import app.viaverse.identity.auth.application.port.out.PartialAuthTokenStore;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PartialAuthTokenValkeyAdapter implements PartialAuthTokenStore {

    private final StringRedisTemplate redis;

    public PartialAuthTokenValkeyAdapter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void save(String tokenHash, UUID accountId, Duration ttl) {
        redis.opsForValue().set(ValkeyKeyScheme.partialAuthToken(tokenHash), accountId.toString(), ttl);
    }

    @Override
    public Optional<UUID> findAccountId(String tokenHash) {
        String value = redis.opsForValue().get(ValkeyKeyScheme.partialAuthToken(tokenHash));
        return Optional.ofNullable(value).map(UUID::fromString);
    }

    @Override
    public void delete(String tokenHash) {
        redis.delete(ValkeyKeyScheme.partialAuthToken(tokenHash));
    }
}
