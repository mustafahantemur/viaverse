package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record SubmitOfferRequest(
        @PositiveOrZero long amountMinor,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency,
        @Size(max = 1000) String message
) {
}
