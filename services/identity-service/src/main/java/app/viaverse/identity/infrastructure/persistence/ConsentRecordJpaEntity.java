package app.viaverse.identity.infrastructure.persistence;

import app.viaverse.identity.domain.auth.ConsentCategory;
import app.viaverse.identity.domain.auth.ConsentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consent_record")
public class ConsentRecordJpaEntity {
    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 64)
    private ConsentType consentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_category", nullable = false, length = 32)
    private ConsentCategory consentCategory;

    @Column(name = "version", nullable = false, length = 64)
    private String version;

    @Column(name = "accepted", nullable = false)
    private boolean accepted;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(name = "source", length = 160)
    private String source;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ConsentRecordJpaEntity() {
    }

    public ConsentRecordJpaEntity(
            UUID id,
            UUID accountId,
            ConsentType consentType,
            ConsentCategory consentCategory,
            String version,
            boolean accepted,
            Instant now,
            String source
    ) {
        this.id = id;
        this.accountId = accountId;
        this.consentType = consentType;
        this.consentCategory = consentCategory;
        this.version = version;
        this.accepted = accepted;
        this.recordedAt = now;
        this.source = source;
        this.createdAt = now;
    }
}
