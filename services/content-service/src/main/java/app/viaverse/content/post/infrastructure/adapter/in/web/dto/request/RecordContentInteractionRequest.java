package app.viaverse.content.post.infrastructure.adapter.in.web.dto.request;

import app.viaverse.content.post.domain.enums.ContentSignalTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public record RecordContentInteractionRequest(
        @NotNull ContentSignalTypeEnum signalType,
        @NotBlank @Size(max = 80) String surface,
        @PositiveOrZero Integer position,
        @PositiveOrZero Long dwellTimeMs,
        UUID sessionId,
        Instant occurredAt
) {
}
