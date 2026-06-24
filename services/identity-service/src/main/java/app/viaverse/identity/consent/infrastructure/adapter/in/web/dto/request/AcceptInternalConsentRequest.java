package app.viaverse.identity.consent.infrastructure.adapter.in.web.dto.request;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AcceptInternalConsentRequest(
        @NotNull ConsentTypeEnum type,
        @NotBlank String version,
        String source
) {
}
