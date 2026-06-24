package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.entity;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.marketplace.marketplace.domain.enums.ServiceRequestStatusEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "service_request")
public class ServiceRequestJpaEntity {

    @Id
    private UUID id;

    @Column(name = "requester_account_id", nullable = false)
    private UUID requesterAccountId;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private MarketplaceServiceCategory category;

    @Column(name = "budget_min_amount_minor")
    private Long budgetMinAmountMinor;

    @Column(name = "budget_max_amount_minor")
    private Long budgetMaxAmountMinor;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "remote_allowed", nullable = false)
    private boolean remoteAllowed;

    @Column(length = 120)
    private String district;

    @Column(length = 120)
    private String city;

    @ElementCollection
    @CollectionTable(name = "service_request_media", joinColumns = @JoinColumn(name = "request_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "media_asset_id", nullable = false)
    private List<UUID> mediaAssetIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ServiceRequestStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    protected ServiceRequestJpaEntity() {
    }

    public ServiceRequestJpaEntity(
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
        this.id = id;
        this.requesterAccountId = requesterAccountId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.budgetMinAmountMinor = budgetMinAmountMinor;
        this.budgetMaxAmountMinor = budgetMaxAmountMinor;
        this.currency = currency;
        this.remoteAllowed = remoteAllowed;
        this.district = district;
        this.city = city;
        this.mediaAssetIds = new ArrayList<>(mediaAssetIds);
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
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
}
