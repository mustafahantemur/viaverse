package app.viaverse.profile.profile.domain.model;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class IndividualProviderProfile {

    private final UUID accountId;
    private final String serviceBlurb;
    private final String availabilitySummary;
    private final boolean acceptsRemote;
    private final Set<MarketplaceServiceCategory> serviceCategories;
    private final String providerTermsVersionAccepted;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public IndividualProviderProfile(
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
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.serviceBlurb = optionalText(serviceBlurb, "serviceBlurb", 200);
        this.availabilitySummary = optionalText(availabilitySummary, "availabilitySummary", 160);
        this.acceptsRemote = acceptsRemote;
        this.serviceCategories = Set.copyOf(serviceCategories == null ? Set.of() : serviceCategories);
        this.providerTermsVersionAccepted = requireText(
                providerTermsVersionAccepted,
                "providerTermsVersionAccepted",
                64
        );
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static IndividualProviderProfile create(
            UUID accountId,
            String serviceBlurb,
            String providerTermsVersionAccepted,
            Instant now
    ) {
        return new IndividualProviderProfile(
                accountId,
                serviceBlurb,
                null,
                false,
                Set.of(),
                providerTermsVersionAccepted,
                now,
                now,
                0
        );
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public IndividualProviderProfile updateSelfView(
            String nextServiceBlurb,
            String nextAvailabilitySummary,
            boolean nextAcceptsRemote,
            Set<MarketplaceServiceCategory> nextServiceCategories,
            Instant now
    ) {
        return new IndividualProviderProfile(
                accountId,
                nextServiceBlurb,
                nextAvailabilitySummary,
                nextAcceptsRemote,
                nextServiceCategories,
                providerTermsVersionAccepted,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    public IndividualProviderProfile acceptCurrentTerms(String version, Instant now) {
        return new IndividualProviderProfile(
                accountId,
                serviceBlurb,
                availabilitySummary,
                acceptsRemote,
                serviceCategories,
                version,
                createdAt,
                Objects.requireNonNull(now, "now"),
                this.version
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
}
