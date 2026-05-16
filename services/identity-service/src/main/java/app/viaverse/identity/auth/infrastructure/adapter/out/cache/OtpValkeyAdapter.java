package app.viaverse.identity.auth.infrastructure.adapter.out.cache;

import app.viaverse.identity.auth.application.port.out.OtpChallengeStore;
import app.viaverse.identity.auth.domain.enums.OtpChallengeStatusEnum;
import app.viaverse.identity.auth.domain.model.OtpChallenge;
import app.viaverse.identity.auth.infrastructure.adapter.out.cache.ValkeyKeyScheme;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class OtpValkeyAdapter implements OtpChallengeStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public OtpValkeyAdapter(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(OtpChallenge challenge) {
        String key = ValkeyKeyScheme.otp(challenge.getFlowId());
        Duration ttl = Duration.between(Instant.now(), challenge.getExpiresAt());
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(new Snapshot(
                    challenge.getId(),
                    challenge.getFlowId(),
                    challenge.getOtpHash(),
                    challenge.getAttempts(),
                    challenge.getMaxAttempts(),
                    challenge.getStatus(),
                    challenge.getExpiresAt(),
                    challenge.getVerifiedAt(),
                    challenge.getCreatedAt()
            ));
            redis.opsForValue().set(key, json, ttl);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize OtpChallenge", e);
        }
    }

    @Override
    public Optional<OtpChallenge> findByFlowId(UUID flowId) {
        String json = redis.opsForValue().get(ValkeyKeyScheme.otp(flowId));
        if (json == null) {
            return Optional.empty();
        }
        try {
            Snapshot s = objectMapper.readValue(json, Snapshot.class);
            return Optional.of(new OtpChallenge(
                    s.id(), s.flowId(), s.otpHash(), s.attempts(), s.maxAttempts(),
                    s.status(), s.expiresAt(), s.verifiedAt(), s.createdAt()
            ));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize OtpChallenge", e);
        }
    }

    @Override
    public void delete(UUID flowId) {
        redis.delete(ValkeyKeyScheme.otp(flowId));
    }

    private record Snapshot(
            UUID id,
            UUID flowId,
            String otpHash,
            int attempts,
            int maxAttempts,
            OtpChallengeStatusEnum status,
            Instant expiresAt,
            Instant verifiedAt,
            Instant createdAt
    ) {}
}
