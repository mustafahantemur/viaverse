package app.viaverse.identity.consent.application.port.out;

import app.viaverse.identity.consent.domain.ConsentTypeEnum;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ConsentRecordRepository {

    void save(Record record);

    Optional<Record> findByAccountIdAndTypeAndVersion(UUID accountId, ConsentTypeEnum type, String version);

    record Record(
            UUID id,
            UUID accountId,
            ConsentTypeEnum type,
            String category,
            String version,
            boolean accepted,
            Instant recordedAt,
            String source
    ) {}
}
