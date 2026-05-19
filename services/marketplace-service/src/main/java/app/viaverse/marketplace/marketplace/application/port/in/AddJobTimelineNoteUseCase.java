package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.JobTimelineEntry;
import java.util.UUID;

public interface AddJobTimelineNoteUseCase {

    JobTimelineEntry execute(Command command);

    record Command(UUID jobId, UUID actorAccountId, String message) {
    }
}
