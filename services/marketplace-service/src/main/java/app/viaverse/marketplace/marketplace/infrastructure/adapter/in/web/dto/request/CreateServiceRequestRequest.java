package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.request;

import app.viaverse.contracts.marketplace.MarketplaceServiceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateServiceRequestRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 2000) String description,
        @NotNull MarketplaceServiceCategory category,
        @PositiveOrZero Long budgetMinAmountMinor,
        @PositiveOrZero Long budgetMaxAmountMinor,
        @Pattern(regexp = "^[A-Z]{3}$") String currency,
        boolean remoteAllowed,
        @Size(max = 120) String district,
        @Size(max = 120) String city,
        List<UUID> mediaAssetIds
) {
}
