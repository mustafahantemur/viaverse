package app.viaverse.identity.consent.application.port.out;

import app.viaverse.identity.consent.domain.ConsentType;
import java.time.Instant;
import java.util.UUID;

public interface ConsentRecordRepository {

    void save(Record record);

    record Record(
            UUID id,
            UUID accountId,
            ConsentType type,
            String category,
            String version,
            boolean accepted,
            Instant recordedAt,
            String source
    ) {}
}
