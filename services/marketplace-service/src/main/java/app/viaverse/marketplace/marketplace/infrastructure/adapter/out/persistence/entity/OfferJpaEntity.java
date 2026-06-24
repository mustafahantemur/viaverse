package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity;

import app.viaverse.marketplace.marketplace.domain.enums.OfferStatusEnum;
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
@Table(name = "offer")
public class OfferJpaEntity {

    @Id
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "provider_account_id", nullable = false)
    private UUID providerAccountId;

    @Column(name = "amount_minor", nullable = false)
    private long amountMinor;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OfferStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    protected OfferJpaEntity() {
    }

    public OfferJpaEntity(
            UUID id,
            UUID requestId,
            UUID providerAccountId,
            long amountMinor,
            String currency,
            String message,
            OfferStatusEnum status,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = id;
        this.requestId = requestId;
        this.providerAccountId = providerAccountId;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public UUID getId() { return id; }
    public UUID getRequestId() { return requestId; }
    public UUID getProviderAccountId() { return providerAccountId; }
    public long getAmountMinor() { return amountMinor; }
    public String getCurrency() { return currency; }
    public String getMessage() { return message; }
    public OfferStatusEnum getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
