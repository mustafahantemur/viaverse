package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import app.viaverse.profile.profile.domain.enums.BusinessSectorEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateBusinessDraftRequest(
        @Size(max = 180) String legalName,
        @Size(max = 180) String tradeName,
        BusinessSectorEnum sector,
        @Size(max = 64) String taxId,
        @Size(max = 240) String addressLine,
        @Size(max = 120) String district,
        @Size(max = 120) String city,
        @Size(max = 120) String country,
        @Size(max = 64) String phone,
        @Email @Size(max = 320) String emailPublic,
        UUID logoMediaId,
        @Size(max = 2000) String openingHoursJson
) {
}
