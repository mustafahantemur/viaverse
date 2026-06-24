package app.viaverse.marketplace.marketplace.application.port.in;

import app.viaverse.marketplace.marketplace.domain.model.Job;
import java.util.List;
import java.util.UUID;

public interface ListCurrentJobsUseCase {

    List<Job> execute(UUID accountId);
}
