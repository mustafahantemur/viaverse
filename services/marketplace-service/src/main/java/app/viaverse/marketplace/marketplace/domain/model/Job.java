package app.viaverse.marketplace.marketplace.domain.model;

import app.viaverse.marketplace.marketplace.domain.enums.JobStatusEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Job {

    private final UUID id;
    private final UUID requestId;
    private final UUID acceptedOfferId;
    private final UUID requesterAccountId;
    private final UUID providerAccountId;
    private final long agreedAmountMinor;
    private final String currency;
    private final JobStatusEnum status;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public Job(
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
        this.id = Objects.requireNonNull(id, "id");
        this.requestId = Objects.requireNonNull(requestId, "requestId");
        this.acceptedOfferId = Objects.requireNonNull(acceptedOfferId, "acceptedOfferId");
        this.requesterAccountId = Objects.requireNonNull(requesterAccountId, "requesterAccountId");
        this.providerAccountId = Objects.requireNonNull(providerAccountId, "providerAccountId");
        this.agreedAmountMinor = requireAmount(agreedAmountMinor);
        this.currency = requireCurrency(currency);
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static Job create(ServiceRequest request, Offer acceptedOffer, Instant now) {
        return new Job(
                UUID.randomUUID(),
                request.getId(),
                acceptedOffer.getId(),
                request.getRequesterAccountId(),
                acceptedOffer.getProviderAccountId(),
                acceptedOffer.getAmountMinor(),
                acceptedOffer.getCurrency(),
                JobStatusEnum.AGREED,
                now,
                now,
                0
        );
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

    public boolean hasParticipant(UUID accountId) {
        return requesterAccountId.equals(accountId) || providerAccountId.equals(accountId);
    }

    public Job start(Instant now) {
        if (status != JobStatusEnum.AGREED) {
            throw new IllegalStateException("Only agreed jobs can be started");
        }
        return withStatus(JobStatusEnum.IN_PROGRESS, now);
    }

    public Job complete(Instant now) {
        if (status != JobStatusEnum.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress jobs can be completed");
        }
        return withStatus(JobStatusEnum.COMPLETED, now);
    }

    private Job withStatus(JobStatusEnum nextStatus, Instant now) {
        return new Job(
                id,
                requestId,
                acceptedOfferId,
                requesterAccountId,
                providerAccountId,
                agreedAmountMinor,
                currency,
                nextStatus,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    private static long requireAmount(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("agreedAmountMinor must be non-negative");
        }
        return value;
    }

    private static String requireCurrency(String value) {
        if (value == null || !value.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("currency must be a 3-letter ISO code");
        }
        return value;
    }
}
