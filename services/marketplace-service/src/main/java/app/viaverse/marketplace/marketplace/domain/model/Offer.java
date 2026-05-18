package app.viaverse.marketplace.marketplace.domain.model;

import app.viaverse.marketplace.marketplace.domain.enums.OfferStatusEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Offer {

    private final UUID id;
    private final UUID requestId;
    private final UUID providerAccountId;
    private final long amountMinor;
    private final String currency;
    private final String message;
    private final OfferStatusEnum status;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public Offer(
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
        this.id = Objects.requireNonNull(id, "id");
        this.requestId = Objects.requireNonNull(requestId, "requestId");
        this.providerAccountId = Objects.requireNonNull(providerAccountId, "providerAccountId");
        this.amountMinor = requireAmount(amountMinor);
        this.currency = requireCurrency(currency);
        this.message = optionalText(message, "message", 1000);
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static Offer submit(
            UUID requestId,
            UUID providerAccountId,
            long amountMinor,
            String currency,
            String message,
            Instant now
    ) {
        return new Offer(
                UUID.randomUUID(),
                requestId,
                providerAccountId,
                amountMinor,
                currency,
                message,
                OfferStatusEnum.SUBMITTED,
                now,
                now,
                0
        );
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

    public boolean isSubmitted() {
        return status == OfferStatusEnum.SUBMITTED;
    }

    public Offer accept(Instant now) {
        return withStatus(OfferStatusEnum.ACCEPTED, now);
    }

    public Offer reject(Instant now) {
        return withStatus(OfferStatusEnum.REJECTED, now);
    }

    public Offer withdraw(Instant now) {
        if (!isSubmitted()) {
            throw new IllegalStateException("Only submitted offers can be withdrawn");
        }
        return withStatus(OfferStatusEnum.WITHDRAWN, now);
    }

    private Offer withStatus(OfferStatusEnum nextStatus, Instant now) {
        return new Offer(
                id,
                requestId,
                providerAccountId,
                amountMinor,
                currency,
                message,
                nextStatus,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    private static long requireAmount(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("amountMinor must be non-negative");
        }
        return value;
    }

    private static String requireCurrency(String value) {
        if (value == null || !value.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("currency must be a 3-letter ISO code");
        }
        return value;
    }

    private static String optionalText(String value, String field, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }
}
