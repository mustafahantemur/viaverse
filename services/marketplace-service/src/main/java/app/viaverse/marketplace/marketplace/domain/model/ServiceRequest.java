package app.viaverse.marketplace.marketplace.domain.model;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.domain.enums.ServiceRequestStatusEnum;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ServiceRequest {

    private final UUID id;
    private final UUID requesterAccountId;
    private final String title;
    private final String description;
    private final MarketplaceServiceCategory category;
    private final Long budgetMinAmountMinor;
    private final Long budgetMaxAmountMinor;
    private final String currency;
    private final boolean remoteAllowed;
    private final String district;
    private final String city;
    private final List<UUID> mediaAssetIds;
    private final ServiceRequestStatusEnum status;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public ServiceRequest(
            UUID id,
            UUID requesterAccountId,
            String title,
            String description,
            MarketplaceServiceCategory category,
            Long budgetMinAmountMinor,
            Long budgetMaxAmountMinor,
            String currency,
            boolean remoteAllowed,
            String district,
            String city,
            List<UUID> mediaAssetIds,
            ServiceRequestStatusEnum status,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.requesterAccountId = Objects.requireNonNull(requesterAccountId, "requesterAccountId");
        this.title = requireText(title, "title", 160);
        this.description = requireText(description, "description", 2000);
        this.category = Objects.requireNonNull(category, "category");
        this.budgetMinAmountMinor = optionalAmount(budgetMinAmountMinor, "budgetMinAmountMinor");
        this.budgetMaxAmountMinor = optionalAmount(budgetMaxAmountMinor, "budgetMaxAmountMinor");
        if (this.budgetMinAmountMinor != null
                && this.budgetMaxAmountMinor != null
                && this.budgetMinAmountMinor > this.budgetMaxAmountMinor) {
            throw new IllegalArgumentException("budgetMinAmountMinor must not exceed budgetMaxAmountMinor");
        }
        this.currency = requireCurrency(currency);
        this.remoteAllowed = remoteAllowed;
        this.district = optionalText(district, "district", 120);
        this.city = optionalText(city, "city", 120);
        this.mediaAssetIds = List.copyOf(mediaAssetIds == null ? List.of() : mediaAssetIds);
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static ServiceRequest create(
            UUID requesterAccountId,
            String title,
            String description,
            MarketplaceServiceCategory category,
            Long budgetMinAmountMinor,
            Long budgetMaxAmountMinor,
            String currency,
            boolean remoteAllowed,
            String district,
            String city,
            List<UUID> mediaAssetIds,
            Instant now
    ) {
        return new ServiceRequest(
                UUID.randomUUID(),
                requesterAccountId,
                title,
                description,
                category,
                budgetMinAmountMinor,
                budgetMaxAmountMinor,
                currency,
                remoteAllowed,
                district,
                city,
                mediaAssetIds,
                ServiceRequestStatusEnum.OPEN,
                now,
                now,
                0
        );
    }

    public UUID getId() { return id; }
    public UUID getRequesterAccountId() { return requesterAccountId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public MarketplaceServiceCategory getCategory() { return category; }
    public Long getBudgetMinAmountMinor() { return budgetMinAmountMinor; }
    public Long getBudgetMaxAmountMinor() { return budgetMaxAmountMinor; }
    public String getCurrency() { return currency; }
    public boolean isRemoteAllowed() { return remoteAllowed; }
    public String getDistrict() { return district; }
    public String getCity() { return city; }
    public List<UUID> getMediaAssetIds() { return mediaAssetIds; }
    public ServiceRequestStatusEnum getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }

    public boolean isOpen() {
        return status == ServiceRequestStatusEnum.OPEN;
    }

    public ServiceRequest markMatched(Instant now) {
        return new ServiceRequest(
                id,
                requesterAccountId,
                title,
                description,
                category,
                budgetMinAmountMinor,
                budgetMaxAmountMinor,
                currency,
                remoteAllowed,
                district,
                city,
                mediaAssetIds,
                ServiceRequestStatusEnum.MATCHED,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }

    private static String optionalText(String value, String field, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }

    private static Long optionalAmount(Long value, String field) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(field + " must be non-negative");
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
