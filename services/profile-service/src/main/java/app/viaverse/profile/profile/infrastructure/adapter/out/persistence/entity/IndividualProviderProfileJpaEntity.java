package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.web.persistence.BaseJpaEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "individual_provider_profile")
public class IndividualProviderProfileJpaEntity extends BaseJpaEntity {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "service_blurb", length = 200)
    private String serviceBlurb;

    @Column(name = "availability_summary", length = 160)
    private String availabilitySummary;

    @Column(name = "accepts_remote", nullable = false)
    private boolean acceptsRemote;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "individual_provider_service_category",
            joinColumns = @JoinColumn(name = "account_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 64)
    private Set<MarketplaceServiceCategory> serviceCategories = new LinkedHashSet<>();

    @Column(name = "provider_terms_version_accepted", nullable = false, length = 64)
    private String providerTermsVersionAccepted;

    protected IndividualProviderProfileJpaEntity() {
    }

    public IndividualProviderProfileJpaEntity(
            UUID accountId,
            String serviceBlurb,
            String availabilitySummary,
            boolean acceptsRemote,
            Set<MarketplaceServiceCategory> serviceCategories,
            String providerTermsVersionAccepted,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.accountId = accountId;
        this.serviceBlurb = serviceBlurb;
        this.availabilitySummary = availabilitySummary;
        this.acceptsRemote = acceptsRemote;
        this.serviceCategories = new LinkedHashSet<>(serviceCategories == null ? Set.of() : serviceCategories);
        this.providerTermsVersionAccepted = providerTermsVersionAccepted;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getServiceBlurb() {
        return serviceBlurb;
    }

    public String getAvailabilitySummary() {
        return availabilitySummary;
    }

    public boolean isAcceptsRemote() {
        return acceptsRemote;
    }

    public Set<MarketplaceServiceCategory> getServiceCategories() {
        return serviceCategories;
    }

    public String getProviderTermsVersionAccepted() {
        return providerTermsVersionAccepted;
    }
}
