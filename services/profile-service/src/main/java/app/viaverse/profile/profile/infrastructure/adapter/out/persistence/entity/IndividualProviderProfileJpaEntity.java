package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.web.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
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

    @Column(name = "provider_terms_version_accepted", nullable = false, length = 64)
    private String providerTermsVersionAccepted;

    protected IndividualProviderProfileJpaEntity() {
    }

    public IndividualProviderProfileJpaEntity(
            UUID accountId,
            String serviceBlurb,
            String availabilitySummary,
            boolean acceptsRemote,
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

    public String getProviderTermsVersionAccepted() {
        return providerTermsVersionAccepted;
    }
}
