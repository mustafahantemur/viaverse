package app.viaverse.identity.auth.infrastructure.adapter.out.cache;

import app.viaverse.identity.auth.application.port.out.SessionCachePort;
import app.viaverse.identity.auth.domain.model.AuthSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SessionCacheValkeyAdapter implements SessionCachePort {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public SessionCacheValkeyAdapter(
            StringRedisTemplate redis,
            @Qualifier("valkeyObjectMapper") ObjectMapper objectMapper
    ) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @Override
    public void put(AuthSession session, Instant now) {
        Duration ttl = Duration.between(now, session.getExpiresAt());
        if (ttl.isNegative() || ttl.isZero()) {
            evict(session.getId());
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(new Snapshot(
                    session.getAccountId(),
                    session.getStatus(),
                    session.getExpiresAt()
            ));
            redis.opsForValue().set(ValkeyKeyScheme.session(session.getId()), json, ttl);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize session cache snapshot", exception);
        }
    }

    @Override
    public Optional<Snapshot> find(UUID sessionId) {
        String json = redis.opsForValue().get(ValkeyKeyScheme.session(sessionId));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, Snapshot.class));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize session cache snapshot", exception);
        }
    }

    @Override
    public void evict(UUID sessionId) {
        redis.delete(ValkeyKeyScheme.session(sessionId));
    }
}
