package app.viaverse.media.asset.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.Size;

public record CompleteUploadRequest(
        @Size(max = 128) String checksumSha256
) {
}
