package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.response;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import app.viaverse.profile.profile.domain.enums.BusinessSectorEnum;
import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import java.util.Set;
import java.util.UUID;

public record BusinessProfileResponse(
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
        String rejectionReason
) {
}
