package app.viaverse.content.post.infrastructure.adapter.in.web.dto.request;

import app.viaverse.content.post.domain.enums.ContentAuthorModeEnum;
import app.viaverse.content.post.domain.enums.ContentPostTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateContentPostRequest(
        @NotNull ContentAuthorModeEnum authorMode,
        @NotNull ContentPostTypeEnum postType,
        @Size(max = 160) String title,
        @NotBlank @Size(max = 4000) String body,
        @Size(max = 120) String city,
        @Size(max = 120) String district,
        Instant eventStartsAt,
        Instant eventEndsAt,
        List<UUID> mediaAssetIds
) {
}
