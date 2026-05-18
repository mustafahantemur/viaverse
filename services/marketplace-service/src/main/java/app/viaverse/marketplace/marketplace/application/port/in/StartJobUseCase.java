package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.Job;
import java.util.UUID;

public interface StartJobUseCase {

    Job execute(Command command);

    record Command(UUID jobId, UUID providerAccountId) {
    }
}
