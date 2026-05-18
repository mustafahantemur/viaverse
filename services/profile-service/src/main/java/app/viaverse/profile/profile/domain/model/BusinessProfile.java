package app.viaverse.profile.profile.domain.model;

import app.viaverse.profile.profile.domain.enums.BusinessSectorEnum;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class BusinessProfile {

    private final UUID accountId;
    private final String legalName;
    private final String tradeName;
    private final BusinessSectorEnum sector;
    private final String taxId;
    private final String addressLine;
    private final String district;
    private final String city;
    private final String country;
    private final String phone;
    private final String emailPublic;
    private final UUID logoMediaId;
    private final String openingHoursJson;
    private final BusinessVerificationStatusEnum verificationStatus;
    private final String businessTermsVersionAccepted;
    private final String rejectionReason;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public BusinessProfile(
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
            BusinessVerificationStatusEnum verificationStatus,
            String businessTermsVersionAccepted,
            String rejectionReason,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.legalName = optionalText(legalName, "legalName", 180);
        this.tradeName = optionalText(tradeName, "tradeName", 180);
        this.sector = sector;
        this.taxId = optionalText(taxId, "taxId", 64);
        this.addressLine = optionalText(addressLine, "addressLine", 240);
        this.district = optionalText(district, "district", 120);
        this.city = optionalText(city, "city", 120);
        this.country = optionalText(country, "country", 120);
        this.phone = optionalText(phone, "phone", 64);
        this.emailPublic = optionalText(emailPublic, "emailPublic", 320);
        this.logoMediaId = logoMediaId;
        this.openingHoursJson = optionalText(openingHoursJson, "openingHoursJson", 2000);
        this.verificationStatus = Objects.requireNonNull(verificationStatus, "verificationStatus");
        this.businessTermsVersionAccepted = optionalText(
                businessTermsVersionAccepted,
                "businessTermsVersionAccepted",
                64
        );
        this.rejectionReason = optionalText(rejectionReason, "rejectionReason", 240);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    public static BusinessProfile start(UUID accountId, Instant now) {
        return new BusinessProfile(
                accountId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                BusinessVerificationStatusEnum.DRAFT,
                null,
                null,
                now,
                now,
                0
        );
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public BusinessSectorEnum getSector() {
        return sector;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmailPublic() {
        return emailPublic;
    }

    public UUID getLogoMediaId() {
        return logoMediaId;
    }

    public String getOpeningHoursJson() {
        return openingHoursJson;
    }

    public BusinessVerificationStatusEnum getVerificationStatus() {
        return verificationStatus;
    }

    public String getBusinessTermsVersionAccepted() {
        return businessTermsVersionAccepted;
    }

    public String getRejectionReason() {
        return rejectionReason;
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

    public BusinessProfile updateDraft(
            String nextLegalName,
            String nextTradeName,
            BusinessSectorEnum nextSector,
            String nextTaxId,
            String nextAddressLine,
            String nextDistrict,
            String nextCity,
            String nextCountry,
            String nextPhone,
            String nextEmailPublic,
            UUID nextLogoMediaId,
            String nextOpeningHoursJson,
            Instant now
    ) {
        if (verificationStatus == BusinessVerificationStatusEnum.APPROVED) {
            throw new IllegalStateException("Approved business profile cannot be edited as a draft");
        }
        return new BusinessProfile(
                accountId,
                nextLegalName,
                nextTradeName,
                nextSector,
                nextTaxId,
                nextAddressLine,
                nextDistrict,
                nextCity,
                nextCountry,
                nextPhone,
                nextEmailPublic,
                nextLogoMediaId,
                nextOpeningHoursJson,
                BusinessVerificationStatusEnum.DRAFT,
                businessTermsVersionAccepted,
                null,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    public BusinessProfile submit(String acceptedBusinessTermsVersion, Instant now) {
        validateReadyForSubmission();
        return new BusinessProfile(
                accountId,
                legalName,
                tradeName,
                sector,
                taxId,
                addressLine,
                district,
                city,
                country,
                phone,
                emailPublic,
                logoMediaId,
                openingHoursJson,
                BusinessVerificationStatusEnum.SUBMITTED,
                requireText(acceptedBusinessTermsVersion, "acceptedBusinessTermsVersion", 64),
                null,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    public BusinessProfile approve(Instant now) {
        if (verificationStatus != BusinessVerificationStatusEnum.SUBMITTED) {
            throw new IllegalStateException("Only submitted business profiles can be approved");
        }
        return withReviewOutcome(BusinessVerificationStatusEnum.APPROVED, null, now);
    }

    public BusinessProfile reject(String reason, Instant now) {
        if (verificationStatus != BusinessVerificationStatusEnum.SUBMITTED) {
            throw new IllegalStateException("Only submitted business profiles can be rejected");
        }
        return withReviewOutcome(
                BusinessVerificationStatusEnum.REJECTED,
                requireText(reason, "reason", 240),
                now
        );
    }

    public Map<String, String> submissionErrors() {
        java.util.LinkedHashMap<String, String> errors = new java.util.LinkedHashMap<>();
        requirePresent(errors, "legalName", legalName);
        requirePresent(errors, "tradeName", tradeName);
        if (sector == null) {
            errors.put("sector", "must not be null");
        }
        requirePresent(errors, "taxId", taxId);
        requirePresent(errors, "addressLine", addressLine);
        requirePresent(errors, "district", district);
        requirePresent(errors, "city", city);
        requirePresent(errors, "country", country);
        requirePresent(errors, "phone", phone);
        requirePresent(errors, "emailPublic", emailPublic);
        return Map.copyOf(errors);
    }

    private void validateReadyForSubmission() {
        Map<String, String> errors = submissionErrors();
        if (!errors.isEmpty()) {
            throw new IllegalStateException("Business profile is incomplete");
        }
    }

    private BusinessProfile withReviewOutcome(
            BusinessVerificationStatusEnum nextStatus,
            String nextRejectionReason,
            Instant now
    ) {
        return new BusinessProfile(
                accountId,
                legalName,
                tradeName,
                sector,
                taxId,
                addressLine,
                district,
                city,
                country,
                phone,
                emailPublic,
                logoMediaId,
                openingHoursJson,
                nextStatus,
                businessTermsVersionAccepted,
                nextRejectionReason,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    private static void requirePresent(Map<String, String> errors, String field, String value) {
        if (value == null || value.isBlank()) {
            errors.put(field, "must not be blank");
        }
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
