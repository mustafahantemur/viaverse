package app.viaverse.identity.auth.api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record SessionView(
        UUID sessionId,
        String deviceName,
        String platform,
        Instant lastSeenAt,
        boolean current
) {}
