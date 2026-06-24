package app.viaverse.media.asset.infrastructure.adapter.in.web.dto.response;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UploadSessionResponse(
        UUID assetId,
        UUID uploadSessionId,
        URI uploadUrl,
        Map<String, String> requiredHeaders,
        Instant expiresAt
) {
}
