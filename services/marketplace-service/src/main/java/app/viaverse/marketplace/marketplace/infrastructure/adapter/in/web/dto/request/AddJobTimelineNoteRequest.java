package app.viaverse.marketplace.marketplace.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddJobTimelineNoteRequest(
        @NotBlank @Size(max = 1000) String message
) {
}
