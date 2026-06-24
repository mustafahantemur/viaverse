package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.profile.profile.domain.enums.BusinessSectorEnum;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
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
@Table(name = "business_profile")
public class BusinessProfileJpaEntity extends BaseJpaEntity {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "legal_name", length = 180)
    private String legalName;

    @Column(name = "trade_name", length = 180)
    private String tradeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sector", length = 32)
    private BusinessSectorEnum sector;

    @Column(name = "tax_id", length = 64)
    private String taxId;

    @Column(name = "address_line", length = 240)
    private String addressLine;

    @Column(name = "district", length = 120)
    private String district;

    @Column(name = "city", length = 120)
    private String city;

    @Column(name = "country", length = 120)
    private String country;

    @Column(name = "phone", length = 64)
    private String phone;

    @Column(name = "email_public", length = 320)
    private String emailPublic;

    @Column(name = "logo_media_id")
    private UUID logoMediaId;

    @Column(name = "opening_hours_json", length = 2000)
    private String openingHoursJson;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "business_service_category",
            joinColumns = @JoinColumn(name = "account_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 64)
    private Set<MarketplaceServiceCategory> serviceCategories = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 32)
    private BusinessVerificationStatusEnum verificationStatus;

    @Column(name = "business_terms_version_accepted", length = 64)
    private String businessTermsVersionAccepted;

    @Column(name = "rejection_reason", length = 240)
    private String rejectionReason;

    protected BusinessProfileJpaEntity() {
    }

    public BusinessProfileJpaEntity(
            UUID accountId,
            String legalName,
            String tradeName,
            BusinessSectorEnum sector,
            String taxId,
            String addressLine,
            String district,
            String city,
            String country,
            String phone,
            String emailPublic,
            UUID logoMediaId,
            String openingHoursJson,
            Set<MarketplaceServiceCategory> serviceCategories,
            BusinessVerificationStatusEnum verificationStatus,
            String businessTermsVersionAccepted,
            String rejectionReason,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.accountId = accountId;
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.sector = sector;
        this.taxId = taxId;
        this.addressLine = addressLine;
        this.district = district;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.emailPublic = emailPublic;
        this.logoMediaId = logoMediaId;
        this.openingHoursJson = openingHoursJson;
        this.serviceCategories = new LinkedHashSet<>(serviceCategories == null ? Set.of() : serviceCategories);
        this.verificationStatus = verificationStatus;
        this.businessTermsVersionAccepted = businessTermsVersionAccepted;
        this.rejectionReason = rejectionReason;
    }

    public UUID getAccountId() { return accountId; }
    public String getLegalName() { return legalName; }
    public String getTradeName() { return tradeName; }
    public BusinessSectorEnum getSector() { return sector; }
    public String getTaxId() { return taxId; }
    public String getAddressLine() { return addressLine; }
    public String getDistrict() { return district; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getPhone() { return phone; }
    public String getEmailPublic() { return emailPublic; }
    public UUID getLogoMediaId() { return logoMediaId; }
    public String getOpeningHoursJson() { return openingHoursJson; }
    public Set<MarketplaceServiceCategory> getServiceCategories() { return serviceCategories; }
    public BusinessVerificationStatusEnum getVerificationStatus() { return verificationStatus; }
    public String getBusinessTermsVersionAccepted() { return businessTermsVersionAccepted; }
    public String getRejectionReason() { return rejectionReason; }
}
