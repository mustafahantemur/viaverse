package app.viaverse.profile.profile.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface ConsumedEventRepository {

    boolean existsByEventId(UUID eventId);

    void record(UUID eventId, String eventType, Instant consumedAt);
}
