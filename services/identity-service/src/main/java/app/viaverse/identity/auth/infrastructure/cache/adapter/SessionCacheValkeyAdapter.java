package app.viaverse.identity.auth.infrastructure.cache.adapter;

import app.viaverse.identity.auth.domain.enums.SessionStatus;
import app.viaverse.identity.auth.infrastructure.cache.ValkeyKeyScheme;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SessionCacheValkeyAdapter {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public SessionCacheValkeyAdapter(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public void put(UUID sessionId, UUID accountId, SessionStatus status, Instant expiresAt, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(new SessionSnapshot(accountId, status, expiresAt));
            redis.opsForValue().set(ValkeyKeyScheme.session(sessionId), json, ttl);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize session", e);
        }
    }

    public Optional<SessionSnapshot> find(UUID sessionId) {
        String json = redis.opsForValue().get(ValkeyKeyScheme.session(sessionId));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, SessionSnapshot.class));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize session", e);
        }
    }

    public void evict(UUID sessionId) {
        redis.delete(ValkeyKeyScheme.session(sessionId));
    }

    public record SessionSnapshot(UUID accountId, SessionStatus status, Instant expiresAt) {}
}
