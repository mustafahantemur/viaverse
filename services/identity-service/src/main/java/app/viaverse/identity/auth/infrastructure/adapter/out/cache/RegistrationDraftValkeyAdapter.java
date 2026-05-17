package app.viaverse.identity.auth.infrastructure.adapter.out.cache;

import app.viaverse.identity.auth.application.port.out.RegistrationDraftStore;
import app.viaverse.identity.auth.domain.model.RegistrationDraft;
import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import app.viaverse.identity.shared.error.IdentityErrors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Valkey-backed implementation. Serialises the draft to JSON via the
 * application's shared {@link ObjectMapper}; nested fields use a
 * snapshot record so the on-the-wire shape stays stable even if the
 * domain model grows fields it doesn't want to leak into the cache.
 */
@Component
public class RegistrationDraftValkeyAdapter implements RegistrationDraftStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public RegistrationDraftValkeyAdapter(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(RegistrationDraft draft, Duration ttl) {
        try {
            Snapshot snapshot = Snapshot.of(draft);
            redis.opsForValue().set(
                    ValkeyKeyScheme.registrationDraft(draft.getId()),
                    objectMapper.writeValueAsString(snapshot),
                    ttl
            );
        } catch (Exception exception) {
            // Treat draft-store failures the same way we treat rate-limit-store
            // failures: surface as a technical error instead of a silent
            // continue, since the user's next request can't succeed without it.
            throw IdentityErrors.rateLimitBackendUnavailable(exception);
        }
    }

    @Override
    public Optional<RegistrationDraft> findById(UUID draftId) {
        String raw = redis.opsForValue().get(ValkeyKeyScheme.registrationDraft(draftId));
        if (raw == null) {
            return Optional.empty();
        }
        try {
            Snapshot snapshot = objectMapper.readValue(raw, Snapshot.class);
            return Optional.of(snapshot.toDomain());
        } catch (Exception exception) {
            // Bad JSON shouldn't crash the request — treat as missing draft so
            // the client gets a clean "expired" error instead of a 500.
            redis.delete(ValkeyKeyScheme.registrationDraft(draftId));
            return Optional.empty();
        }
    }

    @Override
    public void delete(UUID draftId) {
        redis.delete(ValkeyKeyScheme.registrationDraft(draftId));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record Snapshot(
            UUID id,
            String email,
            String phone,
            String displayName,
            String firstName,
            String lastName,
            String passwordHash,
            List<ConsentTypeEnum> acceptedRequiredConsents,
            boolean marketingConsentAccepted,
            UUID emailFlowId,
            Instant emailVerifiedAt,
            UUID phoneFlowId,
            Instant phoneVerifiedAt,
            Instant createdAt
    ) {
        static Snapshot of(RegistrationDraft draft) {
            return new Snapshot(
                    draft.getId(),
                    draft.getEmail(),
                    draft.getPhone(),
                    draft.getDisplayName(),
                    draft.getFirstName(),
                    draft.getLastName(),
                    draft.getPasswordHash(),
                    draft.getAcceptedRequiredConsents(),
                    draft.isMarketingConsentAccepted(),
                    draft.getEmailFlowId(),
                    draft.getEmailVerifiedAt(),
                    draft.getPhoneFlowId(),
                    draft.getPhoneVerifiedAt(),
                    draft.getCreatedAt()
            );
        }

        RegistrationDraft toDomain() {
            return new RegistrationDraft(
                    id, email, phone, displayName, firstName, lastName, passwordHash,
                    acceptedRequiredConsents == null ? List.of() : acceptedRequiredConsents,
                    marketingConsentAccepted,
                    emailFlowId, emailVerifiedAt,
                    phoneFlowId, phoneVerifiedAt,
                    createdAt
            );
        }
    }
}
