package app.viaverse.identity.auth.infrastructure.adapter.out.cache;

import app.viaverse.identity.auth.application.port.out.RegistrationTokenStore;
import app.viaverse.identity.auth.infrastructure.adapter.out.cache.ValkeyKeyScheme;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RegistrationTokenValkeyAdapter implements RegistrationTokenStore {

    private final StringRedisTemplate redis;

    public RegistrationTokenValkeyAdapter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void save(String tokenHash, UUID flowId, Duration ttl) {
        redis.opsForValue().set(ValkeyKeyScheme.registrationToken(tokenHash), flowId.toString(), ttl);
    }

    @Override
    public Optional<UUID> findFlowId(String tokenHash) {
        String value = redis.opsForValue().get(ValkeyKeyScheme.registrationToken(tokenHash));
        return Optional.ofNullable(value).map(UUID::fromString);
    }

    @Override
    public void delete(String tokenHash) {
        redis.delete(ValkeyKeyScheme.registrationToken(tokenHash));
    }
}
