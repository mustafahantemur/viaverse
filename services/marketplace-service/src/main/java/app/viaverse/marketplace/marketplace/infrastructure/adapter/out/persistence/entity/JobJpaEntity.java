package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity;

import app.viaverse.marketplace.marketplace.domain.enums.JobStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job")
public class JobJpaEntity {

    @Id
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "accepted_offer_id", nullable = false)
    private UUID acceptedOfferId;

    @Column(name = "requester_account_id", nullable = false)
    private UUID requesterAccountId;

    @Column(name = "provider_account_id", nullable = false)
    private UUID providerAccountId;

    @Column(name = "agreed_amount_minor", nullable = false)
    private long agreedAmountMinor;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private JobStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    protected JobJpaEntity() {
    }

    public JobJpaEntity(
            UUID id,
            UUID requestId,
            UUID acceptedOfferId,
            UUID requesterAccountId,
            UUID providerAccountId,
            long agreedAmountMinor,
            String currency,
            JobStatusEnum status,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = id;
        this.requestId = requestId;
        this.acceptedOfferId = acceptedOfferId;
        this.requesterAccountId = requesterAccountId;
        this.providerAccountId = providerAccountId;
        this.agreedAmountMinor = agreedAmountMinor;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public UUID getId() { return id; }
    public UUID getRequestId() { return requestId; }
    public UUID getAcceptedOfferId() { return acceptedOfferId; }
    public UUID getRequesterAccountId() { return requesterAccountId; }
    public UUID getProviderAccountId() { return providerAccountId; }
    public long getAgreedAmountMinor() { return agreedAmountMinor; }
    public String getCurrency() { return currency; }
    public JobStatusEnum getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
